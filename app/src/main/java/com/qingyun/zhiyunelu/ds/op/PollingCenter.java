package com.qingyun.zhiyunelu.ds.op;

import com.qingyun.zhiyunelu.ds.App;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import velites.android.utility.misc.RxHelper;
import velites.java.utility.misc.RxUtil;

public class PollingCenter {

    private final App.Assistant assistant;
    private Disposable working;

    public PollingCenter(App.Assistant assistant) {
        this.assistant = assistant;
        this.assistant.getApi().getLoginStateChanged().subscribe(o -> this.startPolling(false), RxUtil.simpleErrorConsumer);
    }

    public void startPolling(boolean isJustCompleted) {
        Observable.just(0)
                .observeOn(RxHelper.createKeepingScopeSingleSchedule())
                .subscribe(o -> checkStartPolling(isJustCompleted), RxUtil.simpleErrorConsumer);
    }

    private void checkStartPolling(boolean isJustCompleted) {
        if (!assistant.getApi().isLoggedIn()) {
            if (this.working != null) {
                this.working.dispose();
                this.working = null;
            }
        } else {
            if (isJustCompleted || this.working == null) {
                this.working = Observable.just(0).delay(isJustCompleted ? assistant.getSetting().logic.pollingIntervalMs : 0, TimeUnit.MILLISECONDS)
                        .observeOn(RxHelper.createKeepingScopeComputationSchedule())
                        .subscribe(Observable -> doPolling(), ex -> {
                            RxUtil.handleRxExceptionByDefault(ex);
                            startPolling(true);
                        });
            }
        }
    }

    private void doPolling() {
        assistant.getWechat().syncWxDatabase();
        assistant.getSms().syncSmsData();
        assistant.getPhone().uploadErrorRecords();
        assistant.getPhone().uploadUnmatchedSoundRecords();
        startPolling(true);
    }

}
