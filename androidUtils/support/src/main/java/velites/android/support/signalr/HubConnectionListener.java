package velites.android.support.signalr;

public interface HubConnectionListener {
    void onConnected();

    void onDisconnected(int code, String reason);

    void onMessage(HubMessage message);

    void onError(Throwable exception);
}
