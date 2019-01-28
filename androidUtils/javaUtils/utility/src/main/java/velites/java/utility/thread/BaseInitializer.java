package velites.java.utility.thread;

import velites.java.utility.generic.Action1;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.StringUtil;

/**
 * Created by regis on 17/4/20.
 */

public abstract class BaseInitializer<TState> {

    /**
     * @param directlyInit
     * @param state If {@code directlyInit} is {@code false} this will be ignored.
     */
    protected BaseInitializer(boolean directlyInit, TState state) {
        if (directlyInit) {
            ensureInit(state);
        }
    }

    private final Once initializer = new Once(false, new Action1<TState>() {
        @Override
        public void a(TState arg1) {
            doInit(arg1);
        }
    });

    /*
     *
     * @return null means not use new thread.
     */
    protected String getThreadName() {
        return StringUtil.formatInvariant("initializer(%s)", this);
    }

    protected abstract void doInit(TState state);

    public final void ensureInit(final TState state) {
        String tname = getThreadName();
        if (tname == null) {
            initializer.run(state);
        } else {
            ThreadUtil.runInNewThread(new Runnable() {
                @Override
                public void run() {
                    initializer.run(state);
                }
            }, tname, null);
        }
    }

    public final void awaitInitialized() throws InterruptedException {
        initializer.awaitInitialized();
    }

    /**
     *
     * @param swallowLogLevel {@code null} to rethrow with wrapper of {@link RuntimeException} once {@link InterruptedException} happened, otherwise swallow with specified log level.
     * @return {@code false} means {@link InterruptedException} happened, {@code true} means normal process completed.
     */
    public final boolean awaitInitializedNoThrows(Integer swallowLogLevel) {
        try {
            awaitInitialized();
            return true;
        } catch (InterruptedException e) {
            if (swallowLogLevel == null) {
                ExceptionUtil.rethrowAsRuntime(e);
            } else {
                ExceptionUtil.swallowThrowable(e, swallowLogLevel, this, "awaitInitializedNoThrows");
            }
        }
        return false;
    }

    public final void ensureInitBlocking(TState state) throws InterruptedException {
        ensureInit(state);
        awaitInitialized();
    }

    /**
     *
     * @param swallowLogLevel {@code null} to rethrow with wrapper of {@link RuntimeException} once {@link InterruptedException} happened, otherwise swallow with specified log level.
     * @return {@code false} means {@link InterruptedException} happened, {@code true} means normal process completed.
     */
    public final boolean ensureInitBlockingNoThrows(TState state, Integer swallowLogLevel) {
        ensureInit(state);
        return awaitInitializedNoThrows(swallowLogLevel);
    }
}
