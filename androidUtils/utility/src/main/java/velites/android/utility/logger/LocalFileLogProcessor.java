package velites.android.utility.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;

import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogProcessor;
import velites.java.utility.misc.DateTimeUtil;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.FileUtil;
import velites.java.utility.misc.PathUtil;
import velites.java.utility.misc.StringUtil;

public class LocalFileLogProcessor implements LogProcessor {

    private static final String LOG_TAG_DEFAULT = LocalFileLogProcessor.class.getSimpleName();
    private static final DateFormat DATA_FORMAT = DateTimeUtil.DEFAULT_DATEFORMAT_DATE_SIMPLE;
    private static final String LOG_FILE_NAME_FORMAT = "%s.log";


    private final File logFileRoot;
    private final Integer levelLimit;
    private long writerDateStamp;
    private OutputStreamWriter writer = null;

    public LocalFileLogProcessor(File logFileRoot, Integer levelLimit) {
        this.logFileRoot = logFileRoot;
        this.levelLimit = levelLimit;
    }

    public LocalFileLogProcessor(File logFileRoot) {
        this(logFileRoot, null);
    }

    private void clearLogFile() {
        if (writer != null) {
            ExceptionUtil.runAndRethrowAsRuntime(() -> {
                writer.flush();
                writer.close();
            }, () -> writer = null);
        }
    }

    private void createWriter(Date d) throws IOException {
        File f = new File(PathUtil.combine(true, logFileRoot.getPath(), StringUtil.formatInvariant(LOG_FILE_NAME_FORMAT, DATA_FORMAT.format(d))).getFullPath());
        f = FileUtil.ensureFileExists(f);
        writer = new OutputStreamWriter(new FileOutputStream(f));
        writerDateStamp = d.getTime();
    }

    @Override
    public void log(final LogEntry entry) {
        if (this.levelLimit != null && entry.level < this.levelLimit) {
            return;
        }
        String content = entry.obtainLogContent();
        if(StringUtil.isNullOrEmpty(content)){
            return;
        }
        ExceptionUtil.runAndRethrowAsRuntime(() -> {
            Date d = DateTimeUtil.nowDate(null).getTime();
            if (writer == null || writerDateStamp != d.getTime()) {
                this.clearLogFile();
                this.createWriter(d);
            }
            writer.write(content + StringUtil.STRING_NEW_LINE);
            writer.flush();
        }, null);
    }

    @Override
    public void handleError(Throwable ex) {
    }

    @Override
    public void recycle() {
        this.clearLogFile();
    }
}
