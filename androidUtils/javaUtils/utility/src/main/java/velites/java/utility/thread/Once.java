package velites.java.utility.thread;

import java.util.concurrent.CountDownLatch;

import velites.java.utility.generic.Action1;

/**
 * Created by regis on 17/4/20.
 */

public class Once<TState> {

    private final boolean blocking;
    private final Action1<TState> predefinedAct;
    private final CountDownLatch sync = new CountDownLatch(1);
    private volatile boolean hasRun = false; //CONSIDER: to support reset

    public Once(boolean blocking, Action1<TState> predefinedAct) {

        this.blocking = blocking;
        this.predefinedAct = predefinedAct;
    }

    public Once(boolean blocking) {
        this(blocking, null);
    }

    public final boolean run(TState state) {
        return run(predefinedAct, state);
    }

    public final boolean run(Action1<TState> act, TState state) {
        boolean runHere = false;
        if (!hasRun) {
            synchronized (sync) {
                if (!hasRun) {
                    if (blocking) {
                        doRun(act, state);
                    }
                    hasRun = true;
                    runHere = true;
                }
            }
            if (!blocking && runHere) {
                doRun(act, state);
            }
        }
        return runHere;
    }

    private void doRun(Action1<TState> act, TState state) {
        try {
            if (act != null) {
                act.a(state);
            }
        } finally {
            sync.countDown();
        }
    }

    public final void awaitInitialized() throws InterruptedException {
        sync.await();
    }
}
