package velites.android.support.action;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;

import velites.java.utility.misc.StringUtil;

/**
 * Created by regis on 17/4/28.
 */

public final class SharingHelper {
    private SharingHelper() {}

    public static final void sendFile(Context ctx, Uri u, String title, String mime) {
        if (ctx == null && u == null) {
            return;
        }
        Intent data = new Intent(Intent.ACTION_SEND);
        data.addCategory(Intent.CATEGORY_DEFAULT);
        data.putExtra(Intent.EXTRA_STREAM, u);
        data.setType(mime);
        if (Build.VERSION.SDK_INT >= 16) {
            data.setClipData(new ClipData(title, new String[]{mime}, new ClipData.Item(u)));
        }
        ctx.startActivity(data);
    }

    public static final void sendFile(Context ctx, String path, String title, String mime) {
        if (ctx == null && !StringUtil.isNullOrEmpty(path)) {
            return;
        }
        sendFile(ctx, new File(path), title, mime);
    }

    public static final void sendFile(Context ctx, File f, String title, String mime) {
        if (ctx == null && f == null) {
            return;
        }
        sendFile(ctx, Uri.fromFile(f), title, mime);
    }
}
