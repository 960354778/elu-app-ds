package com.qingyun.zhiyunelu.ds.op;

import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.TaskMessage;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import velites.android.utility.misc.RxHelper;

public class MessagingCenter {
    private final App.Assistant assistant;
    private TaskMessage latestTask;

    public MessagingCenter(App.Assistant assistant) {
        this.assistant = assistant;
    }

    public Observable<Boolean> syncTaskMessages() {
        Subject<Boolean> ret = PublishSubject.create();
        this.assistant.getApi().createAsyncApi().obtainDialingInformation()
        .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeComputationSchedule())
                .subscribe(new Observer<ApiResult<TaskMessage>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        ret.onSubscribe(disposable);
                    }

                    @Override
                    public void onNext(ApiResult<TaskMessage> res) {
                        latestTask = res == null ? null : res.data;
                        ret.onNext(latestTask != null);
                    }

                    @Override
                    public void onError(Throwable ex) {
                        ret.onError(ex);
                    }

                    @Override
                    public void onComplete() {
                        ret.onComplete();
                    }
                });
        return ret;
    }

    public TaskMessage[] getTasks() {
        return new TaskMessage[] {latestTask};
    }
}
