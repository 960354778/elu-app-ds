package velites.java.utility.log;

import velites.java.utility.thread.RunnableKeepingScope;
import velites.java.utility.thread.ScopingUtil;

public final class LogStub {

    public static final String LOGGING_SCOPE = "logging";

    public static final int LOG_LEVEL_VERBOSE = 100;
    public static final int LOG_LEVEL_DEBUG = 300;
    public static final int LOG_LEVEL_INFO = 500;
    public static final int LOG_LEVEL_WARNING = 700;
    public static final int LOG_LEVEL_ERROR = 900;

    public static String defaultCategory;
    public static String getDefaultCategory() {
        return defaultCategory;
    }
    public static void setDefaultCategory(String category) {
        defaultCategory = category;
    }

    private static LogProcessor processor;
    public static void setProcessor(LogProcessor p) {
        LogProcessor origin = processor;
        processor = p;
        if ((origin != null) && (origin != p)) {
            origin.recycle();
        }
    }

    private static LogFormatter formatter = new DefaultLogFormatter();
    /**
     * Initialed value is set to {@link DefaultLogFormatter}
     *
     * @param f Will do nothing if passed null, instead of set null
     */
    public static void setFormatter(LogFormatter f) {
        LogFormatter origin = formatter;
        formatter = f;
        if ((origin != null) && (origin != f)) {
            origin.recycle();
        }
    }

    public static void log(final LogEntry entry) {
        if (entry == null) {
            return;
        }
        ScopingUtil.RunInScope(new RunnableKeepingScope(new Runnable() {
            @Override
            public void run() {
                entry.formatter = formatter;
                LogProcessor processor = LogStub.processor;
                if (processor != null) {
                    try {
                        processor.log(entry);
                    } catch (Throwable ex) {
                        try {
                            processor.handleError(ex);
                        } catch (Throwable e) {
                            // Silent
                        }
                    }
                }
            }
        }), LOGGING_SCOPE);
    }
}
