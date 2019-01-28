package velites.java.utility.log;

import java.text.DateFormat;

import velites.java.utility.misc.DateTimeUtil;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.StringUtil;

/**
 * Created by regis on 17/4/20.
 */

public class DefaultLogFormatter implements LogFormatter {

    private static final String LOG_FORMAT_DEFAULT = "%s(%d:%s) {%d} => (%s@%x): %s";
    private static final DateFormat LOG_TIMESTAMP_FORMAT = DateTimeUtil.DEFAULT_DATEFORMAT_DATETIME_DETAIL;

    @Override
    public String format(LogEntry entry) {
        String msg = entry.content;
        if (entry.formatArgs != null) {
            msg = StringUtil.formatInvariant(msg, entry.formatArgs);
        }
        String text = StringUtil.formatInvariant(LOG_FORMAT_DEFAULT, LOG_TIMESTAMP_FORMAT.format(entry.timestamp),
                (entry.runner == null ? 0 : entry.runner.getId()), (entry.runner == null ? StringUtil.STRING_EMPTY
                        : entry.runner.getName()), entry.level,
                (entry.owner == null ? StringUtil.STRING_EMPTY : entry.owner.toString()), (entry.owner == null ? 0
                        : entry.owner.hashCode()), msg);
        if (entry.stacktrace != null) {
            text += StringUtil.STRING_NEW_LINE + ExceptionUtil.extractStacktrace(entry.stacktrace);
        }
        return text;
    }

    @Override
    public void recycle() {
    }
}
