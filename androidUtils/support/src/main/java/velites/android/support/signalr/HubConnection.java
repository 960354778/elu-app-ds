package velites.android.support.signalr;

public interface HubConnection {
    enum State {
        Disconnected, Connecting, Connected, Disconnecting
    }

    void connect();

    void disconnect();

    State getState();

    void addListener(HubConnectionListener listener);

    void removeListener(HubConnectionListener listener);

    void subscribeToEvent(String eventName, HubEventListener eventListener);

    void unSubscribeFromEvent(String eventName, HubEventListener eventListener);

    void invoke(String event, Object... parameters);
}
