package velites.java.utility.log;

import velites.java.utility.dispose.Disposable;

public interface LogProcessor extends Disposable {

    /**
     * @param entry This is ensured not be null
     */
    void log(LogEntry entry);

    void handleError(Throwable ex);
}
