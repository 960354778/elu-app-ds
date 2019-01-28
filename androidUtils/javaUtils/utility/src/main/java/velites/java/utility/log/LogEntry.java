package velites.java.utility.log;

import java.util.Date;

import velites.java.utility.generic.Action1;

public class LogEntry<T extends LogEntry> {

    private volatile boolean prepared;
    public Action1<LogEntry> prepare;
    public int level;
    public Thread runner;
    public String category;
    public Object owner;
    public Date timestamp;
    public String content;
    public Object[] formatArgs;
    public Object state;
    public StackTraceElement[] stacktrace;
    LogFormatter formatter;

    public LogEntry() {
        timestamp = new Date();
        runner = Thread.currentThread();
        category = LogStub.getDefaultCategory();
    }

    public LogEntry(int level, Object owner) {
        this();

        this.level = level;
        this.owner = owner;
    }

    public LogEntry(int level, Object owner, String content, Object... args) {
        this(level, owner);

        this.content = content;
        formatArgs = args;
    }

    public LogEntry(int level, Object owner, Action1<LogEntry> prepare, Object state) {
        this(level, owner);

        this.prepare = prepare;
        this.state = state;
    }

    public void ensurePrepared() {
        if (!prepared) {
            synchronized (this) {
                if (!prepared) {
                    prepared = true;
                    if (prepare != null) {
                        prepare.a(this);
                    }
                }
            }
        }
    }

    public String obtainLogContent() {
        ensurePrepared();
        return formatter.format(this);
    }

    public T setPrepare(Action1<LogEntry> prepare) {
        this.prepare = prepare;
        return (T)this;
    }

    public T setLevel(int level) {
        this.level = level;
        return (T)this;
    }

    public T setRunner(Thread runner) {
        this.runner = runner;
        return (T)this;
    }

    public T setStacktrace(StackTraceElement[] stacktrace) {
        this.stacktrace = stacktrace;
        return (T)this;
    }

    public T applyStacktrace() {
        Thread thd = runner;
        if (thd != null) {
            stacktrace = thd.getStackTrace();
        }
        return (T)this;
    }

    public T setCategory(String category) {
        this.category = category;
        return (T)this;
    }

    public T setOwner(Object owner) {
        this.owner = owner;
        return (T)this;
    }

    public T setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return (T)this;
    }

    public T setContent(String content) {
        this.content = content;
        return (T)this;
    }

    public T setFormatArgs(Object[] formatArgs) {
        this.formatArgs = formatArgs;
        return (T)this;
    }

    public T setState(Object state) {
        this.state = state;
        return (T)this;
    }
}
