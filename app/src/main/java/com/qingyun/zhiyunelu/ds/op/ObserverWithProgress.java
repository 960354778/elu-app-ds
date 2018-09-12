package com.qingyun.zhiyunelu.ds.op;

import android.app.Dialog;
import android.content.Context;

import com.qingyun.zhiyunelu.ds.ui.Popups;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import velites.android.utility.misc.ThreadHelper;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.thread.RunnableKeepingScope;

public abstract class ObserverWithProgress<T> implements Observer<T>{
    private final Context context;
    private Dialog loading;
    private Disposable subscription;

    public ObserverWithProgress(Context context) {
        this.context = context;
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.subscription = d;
        this.checkShowProgress();
    }
    @Override
    public void onNext(T res) {
        this.checkDismissProgress();
    }
    @Override
    public void onComplete() {
    }
    @Override
    public void onError(Throwable ex) {
        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_WARNING, this, "Exception happened in observer: %s", ExceptionUtil.extractException(ex)));
        this.checkDismissProgress();
    }

    protected Context getContext() {
        return context;
    }
    protected Dialog getLoading() {
        return loading;
    }
    protected Disposable getSubscription() {
        return subscription;
    }
    protected void checkShowProgress() {
        if (context != null) {
            ThreadHelper.runOnUiThread(new RunnableKeepingScope(() -> {
                this.loading = Popups.buildProgress(context, true);
            }));
        }
    }
    protected void checkDismissProgress() {
        if (context != null) {
            ThreadHelper.runOnUiThread(new RunnableKeepingScope(() -> {
                if (this.loading != null) {
                    this.loading.hide();
                    this.loading.dismiss();
                }
            }));
        }
    }
}
