package velites.android.support.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import velites.android.support.R;

/**
 * Created by regis on 17/4/25.
 */

public final class DialogHelper {
    private DialogHelper() {}

    public static final AlertDialog.Builder buildAlert(Context ctx, Integer titleResId, Integer msgRegId, DialogInterface.OnClickListener onPositive, DialogInterface.OnClickListener onNegative) {
        AlertDialog.Builder builder = buildAlert(ctx, onPositive, onNegative);
        if (titleResId != null) {
            builder.setTitle(titleResId);
        }
        if (msgRegId != null) {
            builder.setMessage(msgRegId);
        }
        return builder;
    }

    public static final AlertDialog.Builder buildAlert(Context ctx, DialogInterface.OnClickListener onPositive, DialogInterface.OnClickListener onNegative) {
        AlertDialog.Builder builder = buildAlert(ctx, onPositive);
        builder.setNegativeButton(R.string.common_cancel, onNegative);
        return builder;
    }

    public static final AlertDialog.Builder buildAlert(Context ctx, DialogInterface.OnClickListener onPositive) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setPositiveButton(R.string.common_confirm, onPositive);
        return builder;
    }
}
