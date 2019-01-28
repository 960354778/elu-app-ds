package velites.android.utility.misc;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.thread.RunnableKeepingScope;

public class RxHelper {
    private RxHelper() {}

    public static Scheduler createKeepingScopeSchedule(Scheduler scheduler) {
        return new SchedulerWrapperKeepingScope(scheduler);
    }

    public static Scheduler createKeepingScopeMainThreadSchedule() {
        return new SchedulerWrapperKeepingScope(AndroidSchedulers.mainThread());
    }

    public static Scheduler createKeepingScopeIOSchedule() {
        return new SchedulerWrapperKeepingScope(Schedulers.io());
    }

    public static Scheduler createKeepingScopeComputationSchedule() {
        return new SchedulerWrapperKeepingScope(Schedulers.computation());
    }

    public static Scheduler createKeepingScopeSingleSchedule() {
        return new SchedulerWrapperKeepingScope(Schedulers.single());
    }

    public static Scheduler createKeepingScopeTrampolineSchedule() {
        return new SchedulerWrapperKeepingScope(Schedulers.trampoline());
    }

    public static Scheduler createKeepingScopeNewThreadSchedule() {
        return new SchedulerWrapperKeepingScope(Schedulers.newThread());
    }

    public static Scheduler createKeepingScopeSchedule(Executor executor) {
        return new SchedulerWrapperKeepingScope(Schedulers.from(executor));
    }

    public static Scheduler createKeepingScopeSchedule(Looper looper) {
        return new SchedulerWrapperKeepingScope(AndroidSchedulers.from(looper));
    }

    public static Scheduler createKeepingScopeSchedule(HandlerThread thd) {
        return new SchedulerWrapperKeepingScope(AndroidSchedulers.from(thd.getLooper()));
    }

    public static Scheduler createKeepingScopeSchedule(Handler handler) {
        return new SchedulerWrapperKeepingScope(AndroidSchedulers.from(handler.getLooper()));
    }

    private  static class WorkerWrapperKeepingScope extends Scheduler.Worker {
        private final Scheduler.Worker innerWorker;

        public WorkerWrapperKeepingScope(Scheduler.Worker innerWorker) {
            ExceptionUtil.assertArgumentNotNull(innerWorker, "innerWorker");
            this.innerWorker = innerWorker;
        }

        @Override
        public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
            return this.innerWorker.schedule(new RunnableKeepingScope(run), delay, unit);
        }

        @Override
        public void dispose() {
            this.innerWorker.dispose();
        }

        @Override
        public boolean isDisposed() {
            return this.innerWorker.isDisposed();
        }
    }

    private  static class SchedulerWrapperKeepingScope extends Scheduler {
        private final Scheduler innerScheduler;

        public SchedulerWrapperKeepingScope(Scheduler innerScheduler) {
            ExceptionUtil.assertArgumentNotNull(innerScheduler, "innerScheduler");
            this.innerScheduler = innerScheduler;
        }

        @Override
        public Worker createWorker() {
            return this.innerScheduler.createWorker();
        }

        @Override
        public long now(TimeUnit unit) {
            return this.innerScheduler.now(unit);
        }

        @Override
        public void start() {
            this.innerScheduler.start();
        }

        @Override
        public void shutdown() {
            this.innerScheduler.shutdown();
        }

        @Override
        public Disposable scheduleDirect(Runnable run) {
            return this.innerScheduler.scheduleDirect(new RunnableKeepingScope(run));
        }

        @Override
        public Disposable scheduleDirect(Runnable run, long delay, TimeUnit unit) {
            return this.innerScheduler.scheduleDirect(new RunnableKeepingScope(run), delay, unit);
        }

        @Override
        public Disposable schedulePeriodicallyDirect(Runnable run, long initialDelay, long period, TimeUnit unit) {
            return this.innerScheduler.schedulePeriodicallyDirect(new RunnableKeepingScope(run), initialDelay, period, unit);
        }

//        @Override
//        public <S extends Scheduler & Disposable> S when(Function<Flowable<Flowable<Completable>>, Completable> combine) {
//            return this.innerScheduler.when(combine);
//        }
    }
}
