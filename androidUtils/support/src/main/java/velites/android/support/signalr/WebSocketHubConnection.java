package velites.android.support.signalr;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import velites.java.utility.generic.Func0;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.thread.RunnableKeepingScope;

public class WebSocketHubConnection implements HubConnection {
    private static String SPECIAL_SYMBOL = "\u001E";

    private WebSocket client;
    private State state = State.Disconnected;
    private List<HubConnectionListener> listeners = new ArrayList<>();
    private Map<String, List<HubEventListener>> eventListeners = new HashMap<>();
    private final Uri parsedUri;
    private final Func0<OkHttpClient> httpClientProvider;
    private final Gson gson;

    public WebSocketHubConnection(String hubUrl, Func0<OkHttpClient> httpClientProvider, Gson gson) {
        this(Uri.parse(hubUrl), httpClientProvider, gson);
    }

    public WebSocketHubConnection(Uri hubUrl, Func0<OkHttpClient> httpClientProvider, Gson gson) {
        this.parsedUri = hubUrl;
        this.httpClientProvider = httpClientProvider;
        this.gson = gson;
    }

    @Override
    public synchronized void connect() {
        if (this.state != State.Disconnected)
            return;
        this.state = State.Connecting;
        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Starting connect message hub..."));
        Runnable runnable = new RunnableKeepingScope(this::getConnectionId);
        new Thread(runnable).start();
    }

    private void getConnectionId() {
        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Requesting connection id..."));
        if (!(parsedUri.getScheme().equals("http") || parsedUri.getScheme().equals("https"))) {
            throw new RuntimeException("URL must start with http or https");
        }
        try {
            String negotiateUri = parsedUri.buildUpon().appendPath("negotiate").build().toString();
            Request req = new Request.Builder().url(negotiateUri).post(RequestBody.create(MediaType.get("text/plain"), StringUtil.STRING_EMPTY)).build();
            Response res = this.httpClientProvider.f().newCall(req).execute();
            int responseCode = res.code();
            if (responseCode == 200) {
                String result = WebSocketHubConnection.InputStreamConverter.convert(res.body().byteStream());
                JsonElement jsonElement = gson.fromJson(result, JsonElement.class);
                String connectionId = jsonElement.getAsJsonObject().get("connectionId").getAsString();
                JsonElement availableTransportsElements = jsonElement.getAsJsonObject().get("availableTransports");
                List<JsonElement> availableTransports = Arrays.asList(gson.fromJson(availableTransportsElements, JsonElement[].class));
                boolean webSocketAvailable = false;
                for (JsonElement element : availableTransports) {
                    if (element.getAsJsonObject().get("transport").getAsString().equals("WebSockets")) {
                        webSocketAvailable = true;
                        break;
                    }
                }
                if (!webSocketAvailable) {
                    throw new RuntimeException("The server does not support WebSockets transport");
                }
                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Received connection id: %s", connectionId));
                connectClient(connectionId);
            } else if (responseCode == 401) {
                throw new RuntimeException("Unauthorized request");
            } else {
                throw new RuntimeException("Server error");
            }
        } catch (Exception e) {
            synchronized (WebSocketHubConnection.this) {
                this.state = State.Disconnected;
                error(e);
            }
        }
    }

    private synchronized void connectClient(String connectionId) {
        Uri.Builder uriBuilder = parsedUri.buildUpon();
        uriBuilder.appendQueryParameter("id", connectionId);
        Uri uri = uriBuilder.build();
        try {
            Request req = new Request.Builder().url(uri.toString()).build();
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Connecting to web socket: %s", req));
            this.client = this.httpClientProvider.f().newWebSocket(req, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    synchronized (WebSocketHubConnection.this) {
                        state = State.Connected;
                        for (HubConnectionListener listener : listeners) {
                            listener.onConnected();
                        }
                        webSocket.send("{\"protocol\":\"json\",\"version\":1}" + SPECIAL_SYMBOL);
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, this, "Message hub received message: %s", text));
                    String[] messages = text.split(SPECIAL_SYMBOL);
                    for (String m : messages) {
                        SignalRMessage element = gson.fromJson(m, SignalRMessage.class);
                        Integer type = element.getType();
                        if (type != null && type == 1) {
                            HubMessage hubMessage = new HubMessage(element.getInvocationId(), element.getTarget(), element.getArguments());
                            for (HubConnectionListener listener : listeners) {
                                listener.onMessage(hubMessage);
                            }
                            List<HubEventListener> hubEventListeners = eventListeners.get(hubMessage.getTarget());
                            if (hubEventListeners != null) {
                                for (HubEventListener listener : hubEventListeners) {
                                    listener.onEventMessage(hubMessage);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Message hub is closing, code:%d, reason: %s...", code, reason));
                    synchronized (WebSocketHubConnection.this) {
                        state = State.Disconnecting;
                    }
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Message hub closed, code:%d, reason: %s.", code, reason));
                    synchronized (WebSocketHubConnection.this) {
                        state = State.Disconnected;
                        for (HubConnectionListener listener : listeners) {
                            listener.onDisconnected(code, reason);
                        }
                        client = null;
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    synchronized (WebSocketHubConnection.this) {
                        state = State.Disconnected;
                        error(t);
                    }
                }
            });
        } catch (Exception e) {
            synchronized (WebSocketHubConnection.this) {
                this.state = State.Disconnected;
                error(e);
            }
        }
    }

    private void error(Throwable ex) {
        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_WARNING, this, "Message hub got error: %s", ExceptionUtil.extractException(ex)));
        for (HubConnectionListener listener : listeners) {
            listener.onError(ex);
        }
    }

    @Override
    public synchronized void disconnect() {
        if (this.state == State.Disconnected)
            return;
        this.state = State.Disconnecting;
        if (client == null) {
            this.state = State.Disconnected;
        } else {
            client.close(1000, null);
        }
    }

    @Override
    public synchronized State getState() {
        return this.state;
    }

    @Override
    public void addListener(HubConnectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HubConnectionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void subscribeToEvent(String eventName, HubEventListener eventListener) {
        List<HubEventListener> eventMap;
        if (eventListeners.containsKey(eventName)) {
            eventMap = eventListeners.get(eventName);
        } else {
            eventMap = new ArrayList<>();
            eventListeners.put(eventName, eventMap);
        }
        eventMap.add(eventListener);
    }

    @Override
    public void unSubscribeFromEvent(String eventName, HubEventListener eventListener) {
        if (eventListeners.containsKey(eventName)) {
            List<HubEventListener> eventMap = eventListeners.get(eventName);
            eventMap.remove(eventListener);
            if (eventMap.isEmpty()) {
                eventListeners.remove(eventName);
            }
        }
    }

    @Override
    public void invoke(String event, Object... parameters) {
        if (client == null) {
            throw new RuntimeException("Not connected");
        }
        final Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("invocationId", UUID.randomUUID());
        map.put("target", event);
        map.put("arguments", parameters);
        map.put("nonblocking", false);
        Runnable runnable = () -> {
            try {
                client.send(gson.toJson(map) + SPECIAL_SYMBOL);
            } catch (Exception e) {
                error(e);
            }
        };
        new Thread(runnable).start();
    }

    private static class InputStreamConverter {
        private static char RETURN_SYMBOL = '\n';

        static String convert(InputStream stream) throws IOException {
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
                total.append(RETURN_SYMBOL);
            }

            return total.toString();
        }
    }
}