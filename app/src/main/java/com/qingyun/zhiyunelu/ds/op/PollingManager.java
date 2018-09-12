package com.qingyun.zhiyunelu.ds.op;

import com.qingyun.zhiyunelu.ds.App;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import velites.android.utility.misc.RxHelper;
import velites.java.utility.misc.RxUtil;

public class PollingManager {

    private final App.Assistant assistant;
    private Disposable working;

    public PollingManager(App.Assistant assistant) {
        this.assistant = assistant;
        this.assistant.getApi().getTokenChanged().subscribe(o -> this.startPolling(false), RxUtil.simpleErrorConsumer);
    }

    public void startPolling(boolean isJustCompleted) {
        Observable.just(0)
                .observeOn(RxHelper.createKeepingScopeSingleSchedule())
                .subscribe(o -> checkStartPolling(isJustCompleted), RxUtil.simpleErrorConsumer);
    }

    private void checkStartPolling(boolean isJustCompleted) {
        if (assistant.getApi().getToken() == null) {
            if (this.working != null) {
                this.working.dispose();
                this.working = null;
            }
        } else {
            if (isJustCompleted || this.working == null) {
                this.working = Observable.just(0).delay(isJustCompleted ? assistant.getSetting().logic.pollingIntervalMs : 0, TimeUnit.MILLISECONDS)
                        .observeOn(RxHelper.createKeepingScopeComputationSchedule())
                        .subscribe(Observable -> doPolling(), RxUtil.simpleErrorConsumer, () -> startPolling(true));
            }
        }
    }

    private void doPolling() {
        assistant.getWechat().syncWxDatabase();
    }

}
