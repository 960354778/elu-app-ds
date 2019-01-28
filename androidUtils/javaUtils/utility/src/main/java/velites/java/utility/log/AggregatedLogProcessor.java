package velites.java.utility.log;

/**
 * Created by regis on 17/4/25.
 */

public class AggregatedLogProcessor implements LogProcessor {

    private final LogProcessor forError;
    private final LogProcessor[] processors;

    /**
     * @param forError {@code null} mean take the first one of {@code processes}.
     * @param processors
     */
    public AggregatedLogProcessor(LogProcessor forError, LogProcessor... processors) {
        if (forError == null && processors != null && processors.length > 0) {
            forError = processors[0];
        }
        this.forError = forError;
        this.processors = processors;
    }

    @Override
    public void recycle() {
        if (processors != null) {
            for (LogProcessor p : processors) {
                if (p != null && p != forError) {
                    p.recycle();
                }
            }
        }
        if (forError != null) {
            forError.recycle();
        }
    }

    @Override
    public void log(LogEntry entry) {
        if (processors != null) {
            for (LogProcessor p : processors) {
                if (p != null) {
                    p.log(entry);
                }
            }
        }
    }

    @Override
    public void handleError(Throwable ex) {
        if (forError != null) {
            forError.handleError(ex);
        }
    }
}
