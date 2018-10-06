package com.qingyun.zhiyunelu.ds.op;

import android.net.Uri;

import com.google.gson.JsonElement;
import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.TaskMessage;
import com.qingyun.zhiyunelu.ds.ui.TasksActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import velites.android.support.signalr.HubConnection;
import velites.android.support.signalr.HubConnectionListener;
import velites.android.support.signalr.HubMessage;
import velites.android.support.signalr.WebSocketHubConnection;
import velites.android.utility.misc.RxHelper;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.CollectionUtil;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.RxUtil;
import velites.java.utility.misc.SerializationUtil;

public class MessagingCenter {
    private final App.Assistant assistant;
    private final HubConnection hub;
    private TaskMessage latestTask;
    private HubConnectionListener connListener = new HubConnectionListener() {
        @Override
        public void onConnected() {
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Message hub connected"));
        }

        @Override
        public void onDisconnected() {
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Message hub disconnected"));
        }

        @Override
        public void onMessage(HubMessage message) {
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, this, "Message hub received message: %s", SerializationUtil.describe(message)));
        }

        @Override
        public void onError(Exception exception) {
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_WARNING, this, "Message hub got error: %s", ExceptionUtil.extractException(exception)));
        }
    };

    public MessagingCenter(App.Assistant assistant) {
        this.assistant = assistant;
        this.hub = new WebSocketHubConnection(Uri.parse(assistant.getSetting().network.apiRootUrl).buildUpon().appendPath(Constants.Paths.URL_SEGMENT_MESSAGE_HUB).build(), assistant.getApi()::obtainExtraHeaders);
        this.hub.addListener(connListener);
        this.hub.subscribeToEvent(Constants.Logic.EVENT_MESSAGE_HUB_DIALING, this::handleDialingEvent);
        assistant.getApi().getLoginStateChanged().subscribe(loggedIn -> {
            ensureConnection();
        }, RxUtil.simpleErrorConsumer);
        Observable.interval(assistant.getSetting().logic.messageHubAutoReconnectIntervalMs, TimeUnit.MILLISECONDS)
                .observeOn(RxHelper.createKeepingScopeIOSchedule())
                .subscribe(v -> ensureConnection(), RxUtil.simpleErrorConsumer);
    }

    private void ensureConnection() {
        if (assistant.getApi().isLoggedIn()) {
            hub.connect();
        } else {
            hub.disconnect();
        }
    }

    private void handleDialingEvent(HubMessage msg) {
        if (msg != null) {
            JsonElement[] js = msg.getArguments();
            if (!CollectionUtil.isNullOrEmpty(js)) {
                ExceptionUtil.executeWithRetry(() -> {
                    latestTask = assistant.getGson().fromJson(js[0], TaskMessage.class);
                    if (latestTask != null) {
                        TasksActivity.launchMe(assistant.getDefaultContext());
                    }
                }, 1, null);
            }
        }
    }

    public TaskMessage[] getTasks() {
        TaskMessage t = latestTask;
        return t == null ? new TaskMessage[0] : new TaskMessage[] {latestTask};
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
}
