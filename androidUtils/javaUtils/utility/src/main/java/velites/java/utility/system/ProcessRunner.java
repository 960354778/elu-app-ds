package velites.java.utility.system;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import velites.java.utility.ex.StatedException;
import velites.java.utility.generic.Tuple2;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.IOUtil;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.thread.ThreadUtil;

/**
 * Created by regis on 17/5/2.
 */

public final class ProcessRunner {

    public static class Options {
        public boolean throwIfExitNonZero;
    }

    public static final Options defaultOptionsThrowIfExitNonZero = new ProcessRunner.Options(){{throwIfExitNonZero = true;}};

    public interface ExecProcessInterceptor {
        void started();
        void inputFlushed();
        void processResultLine(String line, ResultSource src);
        boolean handleError(Throwable ex, ResultSource src);
        void finished(Integer result, String rStd, String rErr);
    }
    public enum ResultSource {
        STDOUT, ERROR
    }
    public static class ExecFailureException extends RuntimeException {
        public ExecFailureException(int resultValue) {
            super(StringUtil.formatInvariant("Execution failed with result value: %d.", resultValue));
        }
    }

    private final ProcessBuilder pb;
    private final ExecProcessInterceptor interceptor;
    private final String[] inputs;
    private CountDownLatch latch;
    private Tuple2<List<String>, Exception> outStd;
    private Tuple2<List<String>, Exception> outErr;
    private int resultValue;

    private ProcessRunner(ProcessBuilder pb, ExecProcessInterceptor interceptor, String... inputs) {
        this.pb = pb;
        this.interceptor = interceptor;
        this.inputs = inputs;
    }

    private Tuple2<List<String>, Exception> processOutput(final InputStream stream, final ResultSource src) {
        final Tuple2<List<String>, Exception> out = new Tuple2<>(new ArrayList<String>(), null);
        ThreadUtil.runInNewThread(new Runnable() {
            @Override
            public void run() {
                BufferedReader buf = new BufferedReader(new InputStreamReader(stream));
                try {
                    for (String line = buf.readLine(); line != null; line = buf.readLine()) {
                        out.v1.add(line);
                        if (interceptor != null) {
                            interceptor.processResultLine(line, src);
                        }
                    }
                } catch (IOException e) {
                    out.v2 = e;
                } finally {
                    latch.countDown();
                }
            }
        }, null, null);
        return out;
    }

    private void checkHandleErrorOrRethrow(Throwable ex) {
        ResultSource src = null;
        if (ex instanceof StatedException && ((StatedException)ex).getState() instanceof ResultSource) {
            src = (ResultSource)((StatedException)ex).getState();
            ex = ex.getCause();
        }
        if (interceptor == null || !interceptor.handleError(ex, src)) {
            ExceptionUtil.rethrowAsRuntime(ex);
        }
    }

    public void checkThrowExecFailureException() {
        if (resultValue != 0) {
            throw new ExecFailureException(resultValue);
        }
    }

    //TODO: support cancel
    private void exec(Options opt) {
        Process p = null;
        try {
            p = pb.start();
            latch = new CountDownLatch(2);
            if (interceptor != null) {
                interceptor.started();
            }
            if (inputs != null && inputs.length > 0) {
                DataOutputStream s = null;
                try {
                    s = new DataOutputStream(p.getOutputStream());
                    for (String input : inputs) {
                        s.writeBytes(input);
                        if (!input.endsWith(StringUtil.STRING_NEW_LINE)) {
                            s.writeBytes(StringUtil.STRING_NEW_LINE);
                        }
                        s.flush();
                    }
                    if (interceptor != null) {
                        interceptor.inputFlushed();
                    }
                } finally {
                    IOUtil.silentClose(s);
                }
            }
            outStd = processOutput(p.getInputStream(), ResultSource.STDOUT);
            outErr = processOutput(p.getErrorStream(), ResultSource.ERROR);
            resultValue = p.waitFor();
            latch.await();
            if (outStd.v2 != null) {
                throw new StatedException(outStd.v2, ResultSource.STDOUT);
            }
            if (outErr.v2 != null) {
                throw new StatedException(outErr.v2, ResultSource.ERROR);
            }
            if (opt != null && opt.throwIfExitNonZero) {
                checkThrowExecFailureException();
            }
        } catch (Exception e) {
            checkHandleErrorOrRethrow(e);
        } finally {
            if (p != null) {
                p.destroy();
            }
            String rStd = StringUtil.join(false, StringUtil.STRING_NEW_LINE, (Object[]) getStdOutput());
            String rErr = StringUtil.join(false, StringUtil.STRING_NEW_LINE, (Object[]) getErrOutput());
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Output of executed cmd (%s:(%s))=>%s:" + StringUtil.STRING_NEW_LINE + "STDOUT: %s" + StringUtil.STRING_NEW_LINE + "ERROR: %s",
                    pb == null ? null : pb.command(), inputs == null ? null : StringUtil.join(false, ",", (Object[]) inputs), resultValue, rStd, rErr));
            if (interceptor != null) {
                interceptor.finished(resultValue, rStd, rErr);
            }
        }
    }

    public String[] getErrOutput() {
        return outErr == null ? null : outErr.v1.toArray(new String[0]);
    }

    public String[] getStdOutput() {
        return outStd == null ? null : outStd.v1.toArray(new String[0]);
    }

    public int getResultValue() {
        return resultValue;
    }

    public boolean isSuccess() {
        return resultValue == 0;
    }

    public static ProcessRunner runProcess(ProcessBuilder pb, Options opt, ExecProcessInterceptor interceptor, String... inputs) {
        if (pb == null) {
            return null;
        }
        ProcessRunner pr = new ProcessRunner(pb, interceptor, inputs);
        pr.exec(opt);
        return pr;
    }
}
