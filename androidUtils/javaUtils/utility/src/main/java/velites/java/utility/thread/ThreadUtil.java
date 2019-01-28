package velites.java.utility.thread;

import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;

/**
 * Created by regis on 17/4/19.
 */

public final class ThreadUtil {
    private ThreadUtil() {}

    public static final Thread runInNewThread(final Runnable r, String name, ThreadGroup grp) {
        return runInNewThread(r, name, grp, true);
    }

    public static final Thread runInNewThread(final Runnable r, String name, ThreadGroup grp, boolean keepScope) {
        if (r == null) {
            return null;
        }
        Runnable run = r;
        if (keepScope) {
            final ScopingUtil.Transmitter transmitter = ScopingUtil.CreateTransmitter();
            run = new Runnable() {
                @Override
                public void run() {
                    transmitter.Transmit(r);
                }
            };
        }
        Thread thd = name == null ? new Thread(grp, run) : new Thread(grp, run, name);
        thd.start();
        return thd;
    }

    /**
     *
     * @param obj
     * @return true if the wait completed normally. false for being interrupted.
     */
    public static final boolean waitObject(Object obj)
    {
        return waitObject(obj, null);
    }

    /**
     *
     * @param obj
     * @return true if the wait completed normally. false for being interrupted.
     */
    public static final boolean waitObject(Object obj, Long expire)
    {
        boolean ret = false;
        if (obj != null)
        {
            ret = true;
            try
            {
                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, obj, "Tending to wait..."));
                if (expire == null) {
                    obj.wait();
                } else {
                    obj.wait(expire);
                }
            }
            catch (InterruptedException ex)
            {
                ret = false;
                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, obj, "Waiting get interrupted before being notified."));
            }
        }
        return ret;
    }

    public static final boolean notifyObject(Object obj)
    {
        boolean ret = false;
        if (obj != null)
        {
            ret = true;
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, obj, "Notifying..."));
            obj.notify();
        }
        return ret;
    }

    public static final boolean notifyObjectForAll(Object obj)
    {
        boolean ret = false;
        if (obj != null)
        {
            ret = true;
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, obj, "Notifying all..."));
            obj.notifyAll();
        }
        return ret;
    }
}
