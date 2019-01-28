package velites.java.utility.misc;

import java.util.Map;

import velites.java.utility.generic.Action0;
import velites.java.utility.generic.ActionEx0;
import velites.java.utility.generic.Func2;
import velites.java.utility.generic.FuncEx0;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;

public final class ExceptionUtil {
    private ExceptionUtil() {}

    private static final String ARGUMENT_NULL_MESSAGE_FORMAT = "Argument \"%s\" cannot be null.";
    private static final String SWALLOW_EXCEPTION_CONTENT_FORMAT = "Exception happened but got swallowed (%s):" + StringUtil.STRING_NEW_LINE + "%s";
    private static final int SWALLOW_EXCEPTION_DEFAULT_LOG_LEVEL = LogStub.LOG_LEVEL_DEBUG;
    private static final String EXTRACT_EXCEPTION_MESSAGE_FORMAT = "(%s)=> %s"; // %1$: ex.getClass(), %2$: ex.getMessage()
    private static final String EXTRACT_EXCEPTION_CAUSE_SEPARATOR = "<=============== Cause ===============>";
    private static final String EXTRACT_THREAD_FORMAT = "Thread: %d(%s)"; // %1$: thd.getId(), %2$: thd.getName()
    private static final String LOG_UNCAUGHT_EXCEPTION_CONTENT_FORMAT = "Uncaught exception happened:" + StringUtil.STRING_NEW_LINE + "%s" + StringUtil.STRING_NEW_LINE + "All stackTraces:" + StringUtil.STRING_NEW_LINE + "%s";

    public static void assertArgumentNotNull(String arg, String name, boolean includingEmpty) {
        ExceptionUtil.assertArgumentNotNull(arg, name);
        if (includingEmpty && arg.equals(StringUtil.STRING_EMPTY)) {
            throw new IllegalArgumentException(StringUtil.formatInvariant(ExceptionUtil.ARGUMENT_NULL_MESSAGE_FORMAT, name));
        }
    }

    public static void assertArgumentNotNull(Object arg, String name) {
        if (arg == null) {
            throw new IllegalArgumentException(StringUtil.formatInvariant(ExceptionUtil.ARGUMENT_NULL_MESSAGE_FORMAT, name));
        }
    }

    public static void assertArgumentNotNull(Object[] arg, String name, boolean includingEmpty) {
        ExceptionUtil.assertArgumentNotNull(arg, name);
        if (includingEmpty && arg.length == 0) {
            throw new IllegalArgumentException(StringUtil.formatInvariant(ExceptionUtil.ARGUMENT_NULL_MESSAGE_FORMAT, name));
        }
    }

    public static StringBuffer extractStacktrace(StackTraceElement[] stacks, StringBuffer sb) {
        if (sb == null) {
            sb = new StringBuffer();
        }
        if (stacks != null) {
            boolean first = true;
            for (StackTraceElement stack : stacks) {
                if (!first) {
                    sb.append(StringUtil.STRING_NEW_LINE);
                }
                sb.append(stack.toString());
                first = false;
            }
        }
        return sb;
    }

    public static String extractStacktrace(StackTraceElement[] stacks) {
        return extractStacktrace(stacks, null).toString();
    }

    public static StringBuffer extractStacktraces(Map<Thread, StackTraceElement[]> stacks, StringBuffer sb) {
        if (sb == null) {
            sb = new StringBuffer();
        }
        if (stacks != null) {
            boolean first = true;
            for (Map.Entry<Thread, StackTraceElement[]> ety : stacks.entrySet()) {
                if (!first) {
                    sb.append(StringUtil.STRING_NEW_LINE);
                }
                sb.append(StringUtil.formatInvariant(EXTRACT_THREAD_FORMAT, ety.getKey().getId(), ety.getKey().getName()));
                sb.append(StringUtil.STRING_NEW_LINE);
                extractStacktrace(ety.getValue(), sb);
                first = false;
            }
        }
        return sb;
    }

    public static String extractStacktraces(Map<Thread, StackTraceElement[]> stacks) {
        return extractStacktraces(stacks, null).toString();
    }

    public static StringBuffer extractException(Throwable ex, StringBuffer sb) {
        if (sb == null) {
            sb = new StringBuffer();
        }
        if (ex != null) {
            sb.append(StringUtil.formatInvariant(EXTRACT_EXCEPTION_MESSAGE_FORMAT, ex.getClass().getName(), ex.getMessage()));
            sb.append(StringUtil.STRING_NEW_LINE);
            extractStacktrace(ex.getStackTrace(), sb);
            sb.append(StringUtil.STRING_NEW_LINE);
            Throwable cause = ex.getCause();
            if (cause != null) {
                sb.append(EXTRACT_EXCEPTION_CAUSE_SEPARATOR);
                sb.append(StringUtil.STRING_NEW_LINE);
                extractException(cause, sb);
            }
        }
        return sb;
    }

    public static String extractException(Throwable ex) {
        return extractException(ex, null).toString();
    }

    public static void swallowThrowable(Throwable ex) {
        swallowThrowable(ex, null);
    }

    public static void swallowThrowable(Throwable ex, String desc) {
        swallowThrowable(ex, SWALLOW_EXCEPTION_DEFAULT_LOG_LEVEL, null, desc);
    }

    public static void swallowThrowable(Throwable ex, Object logOwner) {
        swallowThrowable(ex, SWALLOW_EXCEPTION_DEFAULT_LOG_LEVEL, logOwner);
    }

    public static void swallowThrowable(Throwable ex, int logLevel, Object logOwner) {
        swallowThrowable(ex, logLevel, logOwner, null);
    }

    public static void swallowThrowable(Throwable ex, int logLevel, Object logOwner, String desc) {
        LogStub.log(new LogEntry(logLevel, logOwner, SWALLOW_EXCEPTION_CONTENT_FORMAT, desc, extractException(ex)));
    }

    public static Thread.UncaughtExceptionHandler wrapperGlobalUncaughtExceptionHandlerWithLog() {
        final Thread.UncaughtExceptionHandler origin = Thread.getDefaultUncaughtExceptionHandler();
        Thread.UncaughtExceptionHandler wrapped = (thread, throwable) -> {
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_ERROR, null, LOG_UNCAUGHT_EXCEPTION_CONTENT_FORMAT, extractException(throwable), extractStacktraces(Thread.getAllStackTraces())).setRunner(thread));
            if (origin != null) {
                origin.uncaughtException(thread, throwable);
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(wrapped);
        return wrapped;
    }

    public static RuntimeException wrapperAsRuntime(Throwable ex) {
        return new RuntimeException(ex);
    }

    public static RuntimeException checkWrapperAsRuntime(Throwable ex) {
        return ex instanceof RuntimeException ? (RuntimeException)ex : wrapperAsRuntime(ex);
    }

    public static void rethrowAsRuntime(Throwable ex, boolean forceWrap) {
        throw forceWrap ? wrapperAsRuntime(ex) : checkWrapperAsRuntime(ex);
    }

    public static void rethrowAsRuntime(Throwable ex) {
        rethrowAsRuntime(ex, false);
    }

    public static <R> R runAndRethrowAsRuntime(FuncEx0<R> aBody, Action0 aFinally) {
        try {
            return aBody.f();
        }
        catch (Throwable ex) {
            throw checkWrapperAsRuntime(ex);
        }
        finally {
            if (aFinally != null) {
                aFinally.a();
            }
        }
    }

    public static void runAndRethrowAsRuntime(ActionEx0 fBody, Action0 aFinally) {
        try {
            fBody.a();
        }
        catch (Throwable ex) {
            throw checkWrapperAsRuntime(ex);
        }
        finally {
            if (aFinally != null) {
                aFinally.a();
            }
        }
    }

    public static boolean executeWithRetry(ActionEx0 act, Integer retryLimit, Func2<Throwable, Integer, Boolean> handleException) {
        if (act == null) {
            return false;
        }
        int tried = 0;
        while (retryLimit == null || retryLimit < 0 || retryLimit > tried++) {
            try {
                act.a();
                return true;
            } catch (Throwable ex) {
                Boolean handled = handleException == null ? null : handleException.f(ex, tried);
                if (handled == null) {
                    swallowThrowable(ex, LogStub.LOG_LEVEL_WARNING, null);
                } else if (handled) {
                    break;
                } else {
                    rethrowAsRuntime(ex);
                }
            }
        }
        return false;
    }
}
