package com.qingyun.zhiyunelu.ds.op;

import com.qingyun.zhiyunelu.ds.App;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import velites.android.utility.misc.RxHelper;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.RxUtil;

public class PollingCenter {

    private final App.Assistant assistant;
    private Disposable working;
    private int runTimes = 0;

    public PollingCenter(App.Assistant assistant) {
        this.assistant = assistant;
        this.assistant.getApi().getLoginStateChanged().subscribe(o -> this.startPolling(false), RxUtil.simpleErrorConsumer);
    }

    public void startPolling(boolean isJustCompleted) {
        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, this, "Tending to start polling, isJustCompleted: %s, passed run times: %s.", isJustCompleted, this.runTimes));
        Observable.just(0)
                .observeOn(RxHelper.createKeepingScopeSingleSchedule())
                .subscribe(o -> checkStartPolling(isJustCompleted), RxUtil.simpleErrorConsumer);
    }

    private void checkStartPolling(boolean isJustCompleted) {
        if (!assistant.getApi().isLoggedIn()) {
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "User logged out, so scheduling is given up. has running polling: %s, passed run times: %s.", this.working == null, this.runTimes));
            if (this.working != null) {
                this.working.dispose();
                this.working = null;
            }
        } else if (isJustCompleted || this.working == null) {
            long delay = isJustCompleted ? assistant.getSetting().logic.pollingIntervalMs : 0;
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Scheduling polling after %d ms, passed run times: %s.", delay, this.runTimes));
            this.working = Observable.just(0).delay(delay, TimeUnit.MILLISECONDS)
                    .observeOn(RxHelper.createKeepingScopeComputationSchedule())
                    .subscribe(Observable -> doPolling());
        } else{
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Another polling is running and not just completed, so scheduling is given up. passed run times: %s.", this.runTimes));
        }
    }

    private void doPolling() {
        try {
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Start polling, current run times: %s.", ++this.runTimes));
            assistant.getWechat().syncWxDatabase();
            assistant.getSms().syncSmsData();
            assistant.getPhone().uploadErrorRecords();
            assistant.getPhone().uploadUnmatchedSoundRecords();
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Finished polling, current run times: %s.", this.runTimes));
        } catch (Throwable ex) {
            ExceptionUtil.swallowThrowable(ex, LogStub.LOG_LEVEL_WARNING, this);
        }
        startPolling(true);
    }

}
