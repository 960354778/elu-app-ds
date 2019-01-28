package velites.java.utility.thread;

public class RunnableKeepingScope implements Runnable {

    private final Runnable actualRunnable;
    private final ScopingUtil.Transmitter transmitter;

    public RunnableKeepingScope(Runnable actualRunnable) {
        this.actualRunnable = actualRunnable;
        this.transmitter = ScopingUtil.CreateTransmitter();
    }

    @Override
    public final void run() {
        this.transmitter.Transmit(actualRunnable);
    }
}
