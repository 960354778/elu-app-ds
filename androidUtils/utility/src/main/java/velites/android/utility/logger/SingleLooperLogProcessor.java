package velites.android.utility.logger;

import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.log.LogProcessor;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.thread.RunnableKeepingScope;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.concurrent.CountDownLatch;

public class SingleLooperLogProcessor implements LogProcessor {
    private static final String DEFAULT_THREAD_NAME = "logger";
    private static final String THREAD_NAME_FORMAT_PREPARING = "%s(prepare)";

    private HandlerThread thread;
    private Handler handler;
    private final CountDownLatch latch = new CountDownLatch(1);
    private final LogProcessor innerLogProcessor;

    public SingleLooperLogProcessor(LogProcessor innerLogProcessor) {
        this(null, innerLogProcessor);
    }

    public SingleLooperLogProcessor(String threadName, LogProcessor innerLogProcessor) {
        ExceptionUtil.assertArgumentNotNull(innerLogProcessor, "innerLogProcessor");
        this.innerLogProcessor = innerLogProcessor;
        if (threadName == null) {
            threadName = DEFAULT_THREAD_NAME;
        }
        thread = new HandlerThread(threadName);
        thread.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler = new Handler(thread.getLooper());
                latch.countDown();
            }
        }, StringUtil.formatInvariant(THREAD_NAME_FORMAT_PREPARING, threadName)).start();
    }

    @Override
    public void log(final LogEntry entry) {
        if (entry != null) {
            if (entry.level >= LogStub.LOG_LEVEL_ERROR) {
                doLog(entry);
            } else {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    ExceptionUtil.swallowThrowable(e, LogStub.LOG_LEVEL_WARNING, StringUtil.formatInvariant("waiting \"%s\" finish initialization", this));
                    return;
                }
                handler.post(new RunnableKeepingScope(() -> doLog(entry)));
            }
        }
    }

    private void doLog(LogEntry entry) {
        try {
            innerLogProcessor.log(entry);
        } catch (Exception ex) {
            handleError(ex);
        }
    }

    @Override
    public void handleError(Throwable ex) {
        try {
            innerLogProcessor.handleError(ex);
        } catch (Throwable e) {
            // Silent
        }
    }

    @Override
    public void recycle() {
        innerLogProcessor.recycle();
        handler = null;
        if (thread == null) {
            thread.quit();
            thread = null;
        }
    }
}
