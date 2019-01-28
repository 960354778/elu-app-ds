package velites.java.utility.thread;


import velites.java.utility.generic.Action0;
import velites.java.utility.generic.Func0;
import velites.java.utility.misc.InfoDescriptor;
import velites.java.utility.misc.SyntaxUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by regis on 17/4/19.
 */

public class Locker extends InfoDescriptor implements Lock {

    private final Lock innerLock;

    public Locker(Func0<String> descriptor, Lock innerLock) {
        super(descriptor);
        this.innerLock = SyntaxUtil.nvl(innerLock, createLockByDefault);
    }

    public Locker(String description, Lock innerLock) {
        super(description);
        this.innerLock = SyntaxUtil.nvl(innerLock, createLockByDefault);
    }

    public Locker(Func0<String> descriptor) {
        this(descriptor, null);
    }

    public Locker(String description) {
        this(description, null);
    }

    public Locker(Lock innerLock) {
        this((Func0<String>)null, innerLock);
    }

    public Locker() {
        this((Func0<String>)null, null);
    }

    private final Func0<Lock> createLockByDefault = new Func0<Lock>() {
        @Override
        public Lock f() {
            return new ReentrantLock();
        }
    };

    @Override
    public void lock() {
        innerLock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        innerLock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return innerLock.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return innerLock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        innerLock.unlock();
    }

    @Override
    public Condition newCondition() {
        return innerLock.newCondition();
    }

    public void runInScopeWithLock(Action0 act) {
        SyncUtil.runWithLock(act, this);
    }

    public void runInScopeWithLockInterruptibly(Action0 act) throws InterruptedException {
        SyncUtil.runWithLockInterruptibly(act, this);
    }

    public void runInScopeWithTryLock(Action0 act) {
        SyncUtil.runWithTryLock(act, this);
    }

    public void runInScopeWithTryLock(Action0 act, long time, TimeUnit unit) throws InterruptedException {
        SyncUtil.runWithTryLock(act, this, time, unit);
    }
}
