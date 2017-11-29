package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import velites.android.support.media.MediaHelper;
import velites.java.utility.generic.Tuple2;
import velites.java.utility.misc.DateTimeUtil;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.thread.BaseInitializer;

/**
 * Created by regis on 17/4/19.
 */

public final class UIAssistant {
    private UIAssistant() {}

    private static BaseInitializer<Tuple2<Context, Intent>> initializer = new BaseInitializer<Tuple2<Context, Intent>>(false, null) {
        @Override
        protected void doInit(Tuple2<Context, Intent> values) {
            baseIntent = values.v2;
        }
    };

    private static Intent baseIntent;

    public static Intent getBaseIntent() {
        initializer.awaitInitializedNoThrows(null);
        return baseIntent;
    }

    public static final void ensureInit(Context ctx, Intent intent) {
        initializer.ensureInit(new Tuple2<Context, Intent>(ctx, intent));
    }
}
