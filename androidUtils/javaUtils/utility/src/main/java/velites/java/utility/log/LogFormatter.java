package velites.java.utility.log;

import velites.java.utility.dispose.Disposable;

/**
 * Created by regis on 17/4/20.
 */

public interface LogFormatter extends Disposable {

    String format(LogEntry entry);
}
