package velites.java.utility.misc;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by regis on 2018/6/5.
 */

public final class IOUtil {
    private IOUtil() {}

    public static void silentClose(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException ex) {
                ExceptionUtil.swallowThrowable(ex);
            }
        }
    }
}
