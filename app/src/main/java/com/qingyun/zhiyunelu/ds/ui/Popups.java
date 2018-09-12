package com.qingyun.zhiyunelu.ds.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.qingyun.zhiyunelu.ds.R;

public final class Popups {
    private Popups() {}

    public static Dialog buildAlert(Context ctx, String message, boolean show) {
        AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        b.setMessage(message);
        b.setPositiveButton(R.string.common_confirm, null);
        AlertDialog ret = b.create();
        if (show) {
            ret.show();
        }
        return ret;
    }

    public static Dialog buildProgress(Context ctx, boolean show) {
//        loading = new ProgressDialog(context);
//        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        loading.setCancelable(false);
////                    loading.setCanceledOnTouchOutside(false);
//        loading.show();
        AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        b.setView(R.layout.dialog_progress);
        b.setCancelable(false);
        AlertDialog ret = b.create();
        if (show) {
            ret.show();
        }
        return ret;
    }
}
