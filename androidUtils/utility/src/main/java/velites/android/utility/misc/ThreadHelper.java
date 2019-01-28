package velites.android.utility.misc;

import android.os.Handler;
import android.os.Looper;

import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.thread.ThreadUtil;

public final class ThreadHelper {
    private ThreadHelper() {}

    public static enum RunInLooperModeKind
    {
        NORMAL, SYNCHRONIZED, FORCE_NEXT_LOOP
    }

    /**
     *
     * @param r
     * @return Whether run in current thread.
     * @remarks (mode = NORMAL)
     */
    public static final boolean runOnUiThread(Runnable r)
    {
        return runOnUiThread(r, RunInLooperModeKind.NORMAL);
    }

    /**
     * @return Whether run in current thread.
     */
    public static final boolean runOnUiThread(Runnable r, RunInLooperModeKind mode)
    {
        return runOnLooper(Looper.getMainLooper(), r, mode);
    }

    /**
     *
     * @param looper
     *            (如果传入null，则视同指定在当前线程执行）
     * @param r
     * @return Whether r is finished.
     */
    public static final boolean runOnLooper(Looper looper, Runnable r, RunInLooperModeKind mode)
    {
        ExceptionUtil.assertArgumentNotNull(r, "r");
        boolean ret = false;
        if ((looper == null) || ((Thread.currentThread() == looper.getThread()) && (mode != RunInLooperModeKind.FORCE_NEXT_LOOP)))
        {
            r.run();
            ret = true;
        }
        else
        {
            final Handler h = new Handler(looper);
            if (mode == RunInLooperModeKind.SYNCHRONIZED)
            {
                runWithHandlerSynchronized(r, h);
                ret = true;
            }
            else
            {
                h.post(r);
            }
        }
        return ret;
    }

    private static void runWithHandlerSynchronized(final Runnable r, final Handler h)
    {
        final Object lock = new Object();
        synchronized (lock)
        {
            Runnable nr = new Runnable()
            {
                @Override
                public void run()
                {
                    synchronized (lock)
                    {
                        r.run();
                        ThreadUtil.notifyObject(lock);
                    }
                }
            };
            h.post(nr);
            ThreadUtil.waitObject(lock);
        }
    }

}
