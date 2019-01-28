package velites.android.utility.root;

import java.io.File;

import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.system.ProcessRunner;

/**
 * Created by luohongzhen on 03/12/2017.
 */

public class RootUtility {
    private static final String OPERATOR_CONCAT_LOGICAL_AND = " && ";

    public static boolean hasRoot() {
        String[] suSearchPath = {"/system/bin/su", "/system/xbin/su", "/sbin/", "/vendor/bin/"};
        try {
            for (String path : suSearchPath) {
                if (new File(path).exists()) {
                    LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, RootUtility.class, "this phone has su cmd path:%s", path));
                    return true;
                }
            }
        } catch (Exception e) {
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_ERROR, RootUtility.class, "hasRoot Exception:%s", e.getStackTrace().toString()));
        }
        return false;
    }

    public static boolean isRooted() {
        return hasRoot() && runAsRoot(null).isSuccess();
    }

    public static void assertRooted() {
        if (!isRooted()) {
            throw new UnsupportedOperationException("Not rooted");
        }
    }

    public static ProcessRunner runAsRoot(ProcessRunner.Options runnerOptions, String... cmds) {
        return ProcessRunner.runProcess(new ProcessBuilder("su"), runnerOptions, null, StringUtil.join(true, OPERATOR_CONCAT_LOGICAL_AND, (Object[]) cmds), "exit");
    }
}
