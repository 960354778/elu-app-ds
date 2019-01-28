package velites.android.support.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import velites.java.utility.generic.Tuple1;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.StringUtil;

/**
 * Created by regis on 17/5/2.
 */

public final class MediaHelper {
    private MediaHelper() {}

    public static Bitmap extractAudioCover(String path) {
        if (StringUtil.isNullOrSpace(path)) {
            return null;
        }
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            byte[] bs = retriever.getEmbeddedPicture();
            if (bs == null) {
                return null;
            }
            return BitmapFactory.decodeByteArray(bs, 0, bs.length);
        } catch (Exception ex) {
            ExceptionUtil.swallowThrowable(ex, StringUtil.formatInvariant("extractAudioCover: \"%s\"", path));
            return null;
        }
    }

    public static boolean deleteMedia(Context ctx, String path, Uri u, String pathColumn, boolean ignorePhysicaFailure) {
        if (ctx == null || path == null || u == null || pathColumn == null) {
            return false;
        }
        if (ctx.getContentResolver().delete(u, StringUtil.formatInvariant("%s = ?", pathColumn), new String[]{path}) > 0) {
            try {
                if (!new File(path).delete()) {
                    LogStub.log(new LogEntry(LogStub.LOG_LEVEL_WARNING, null, "delete \"%s\"", "path"));
                    if (!ignorePhysicaFailure) {
                        return false;
                    }
                }
                return true;
            } catch (Exception ex) {
                ExceptionUtil.swallowThrowable(ex, LogStub.LOG_LEVEL_WARNING, null, StringUtil.formatInvariant("delete \"%s\"", path));
                return false;
            }
        } else {
            return false;
        }
    }

    public static Uri registerMedia(Context ctx, String path) {
        if (ctx == null || path == null) {
            return null;
        }
        final Tuple1<Uri> result = new Tuple1<>(null);
        final CountDownLatch latch = new CountDownLatch(1);
        MediaScannerConnection.scanFile(ctx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String p, Uri uri) {
                result.v1 = uri;
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            ExceptionUtil.swallowThrowable(e, StringUtil.formatInvariant("registerMedia \"%s\"", path));
        }
        return result.v1;
    }

    public static String retrieveMediaDuration(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (path != null) {
                mmr.setDataSource(path);
            }
            return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
            ExceptionUtil.swallowThrowable(ex);
        } finally {
            mmr.release();
        }
        return null;
    }

    public static String retrieveMediaMimetype(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (path != null) {
                mmr.setDataSource(path);
            }
            return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        } catch (Exception ex) {
            ExceptionUtil.swallowThrowable(ex);
        } finally {
            mmr.release();
        }
        return null;
    }
}
