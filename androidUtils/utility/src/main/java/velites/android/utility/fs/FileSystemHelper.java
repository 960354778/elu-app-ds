package velites.android.utility.fs;

import java.io.File;

import velites.java.utility.misc.PathUtil;
import velites.java.utility.misc.StringUtil;

/**
 * Created by regis on 17/4/25.
 */

public final class FileSystemHelper {
    private FileSystemHelper() {}

    public static final String DEFAULT_PATH_TRANSFORM_FOR_RERTY_FORMAT = "%s-%d";

    /**
     * @param path This will be changed inline.
     * @param formatToRetry %1$: origin name, %2$: retry number.
     * @param retryLimit
     * @return
     */
    public static final String determineValidPath(PathUtil.PathInfo path, String formatToRetry, Integer retryLimit) {
        if (path == null) {
            return null;
        }
        String oname = path.getFilenameWithoutExtension();
        if (StringUtil.isNullOrEmpty(formatToRetry)) {
            formatToRetry = DEFAULT_PATH_TRANSFORM_FOR_RERTY_FORMAT;
        }
        String p = null;
        for (int retry = 0; retryLimit == null || retry <= 0 || retry <= retryLimit; retry++) {
            if (retry > 0) {
                path.setFilenameWithoutExtension(StringUtil.formatInvariant(formatToRetry, oname, retry));
            }
            p = path.getFullPath();
            if (!new File(p).exists()) {
                break;
            }
        }
        return p;
    }
}
