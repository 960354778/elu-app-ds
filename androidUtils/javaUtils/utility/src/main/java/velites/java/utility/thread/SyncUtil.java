package velites.java.utility.thread;

import velites.java.utility.generic.Action0;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created by regis on 17/4/19.
 */

public final class SyncUtil {
    private SyncUtil() {}

    private static final boolean checkRunWithoutLock(Action0 act, Lock lock) {
        if (act == null) {
            return true;
        }
        if (lock == null) {
            act.a();
            return true;
        }
        return false;
    }

    private static final void runAfterLocked(Action0 act, Lock lock) {
        try {
            act.a();
        } finally {
            lock.unlock();
        }
    }

    public static final void runWithLock(Action0 act, Lock lock) {
        if (!checkRunWithoutLock(act, lock)) {
            lock.lock();
            runAfterLocked(act, lock);
        }
    }

    public static final boolean runWithTryLock(Action0 act, Lock lock) {
        if (!checkRunWithoutLock(act, lock) && lock.tryLock()) {
            runAfterLocked(act, lock);
            return true;
        }
        return false;
    }

    public static final void runWithLockInterruptibly(Action0 act, Lock lock) throws InterruptedException {
        if (!checkRunWithoutLock(act, lock)) {
            lock.lockInterruptibly();
            runAfterLocked(act, lock);
        }
    }

    public static final boolean runWithTryLock(Action0 act, Lock lock, long time, TimeUnit unit) throws InterruptedException {
        if (!checkRunWithoutLock(act, lock) && lock.tryLock(time, unit)) {
            runAfterLocked(act, lock);
            return true;
        }
        return false;
    }
}
