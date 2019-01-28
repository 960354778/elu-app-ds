package velites.android.utility.logger;

import android.util.Log;

import velites.java.utility.generic.Action3;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.log.LogProcessor;
import velites.java.utility.misc.StringUtil;

public class PrimitiveLogProcessor implements LogProcessor {
    private static final String LOG_TAG_DEFAULT = PrimitiveLogProcessor.class.getSimpleName();
    private static final String LOG_TAG_ERROR_DURING_LOGGING = StringUtil.formatInvariant("%s-error", LOG_TAG_DEFAULT);
    private static final int LOG_SIZE_LIMITATION = 1024;
    private static final String[] LOG_TRUNCATE_DELIMITERS = new String[] {StringUtil.STRING_NEW_LINE, StringUtil.STRING_SPACE};
    @Override
    public void log(LogEntry entry) {
        String content = entry.obtainLogContent();
        final String cat = StringUtil.isNullOrEmpty(entry.category) ? LOG_TAG_DEFAULT : entry.category;
        StringUtil.truncateToActions(content, LOG_SIZE_LIMITATION, LOG_TRUNCATE_DELIMITERS, new Action3<Integer, String, String>() {
            @Override
            public void a(Integer arg1, String arg2, String arg3) {
                if (entry.level >= LogStub.LOG_LEVEL_ERROR) {
                    Log.e(cat, arg3);
                } else if (entry.level >= LogStub.LOG_LEVEL_WARNING) {
                    Log.w(cat, arg3);
                } else if (entry.level >= LogStub.LOG_LEVEL_INFO) {
                    Log.i(cat, arg3);
                } else if (entry.level >= LogStub.LOG_LEVEL_DEBUG) {
                    Log.d(cat, arg3);
                } else if (entry.level >= LogStub.LOG_LEVEL_VERBOSE) {
                    Log.v(cat, arg3);
                }
            }
        });
    }

    @Override
    public void handleError(Throwable ex) {
        Log.w(LOG_TAG_ERROR_DURING_LOGGING, ex);
    }

    @Override
    public void recycle() {
    }
}
