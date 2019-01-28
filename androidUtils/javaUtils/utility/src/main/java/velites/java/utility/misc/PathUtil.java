package velites.java.utility.misc;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by regis on 17/4/20.
 */

public final class PathUtil {
    private PathUtil() {}

    public static final String SEPARATOR_IN_PATH = File.separator; // / or \
    public static final String SEPARATOR_BETWEEN_PATHES = File.pathSeparator; // : or ;
    public static final String SEPARATOR_BEFORE_EXT = ".";
    public static final String SEPARATOR_AFTER_DRIVE = ":"; // for windows
    public static final String SPECIAL_FILE_CURRENT_DIR = ".";
    public static final String SPECIAL_FILE_PARENT_DIR = "..";

    public static final String getExtension(String path, boolean includeSeparator) {
        if (path == null) {
            return null;
        }
        int pos = path.lastIndexOf(SEPARATOR_BEFORE_EXT);
        if (pos >= 0 && path.indexOf(SEPARATOR_IN_PATH, pos) < 0) {
            return path.substring(pos + (includeSeparator ? 0 : 1));
        }
        return StringUtil.STRING_EMPTY;
    }

    public static final String removeExtension(String path) {
        if (path != null) {
            int pos = path.lastIndexOf(SEPARATOR_BEFORE_EXT);
            if (pos >= 0 && path.indexOf(SEPARATOR_IN_PATH, pos) < 0) {
                path = path.substring(0, pos);
            }
        }
        return path;
    }

    public static final String getFilename(String path) {
        if (path != null) {
            int pos = path.lastIndexOf(SEPARATOR_IN_PATH);
            if (pos >= 0) {
                path = path.substring(pos + SEPARATOR_IN_PATH.length());
            }
        }
        return path;
    }

    public static final String getFilenameWithoutExt(String path) {
        return removeExtension(getFilename(path));
    }

    public static final String concat(String... pathes) {
        if (pathes == null) {
            return null;
        }
        String p = StringUtil.STRING_EMPTY;
        for (String path : pathes) {
            if (path == null) {
                continue;
            }
            if (!p.endsWith(SEPARATOR_IN_PATH)) {
                p += SEPARATOR_IN_PATH;
            }
            if (path.startsWith(SEPARATOR_IN_PATH)) {
                p += path.substring(SEPARATOR_IN_PATH.length());
            } else {
                p += path;
            }
        }
        return p;
    }

    public static final PathInfo parse(String path) {
        if (path == null) {
            return null;
        }
        //CONSIDER: detect illegal characters?
        PathInfo info = new PathInfo();
        String[] segs = path.split(SEPARATOR_IN_PATH);
        String head = segs[0]; // here, segs.length() must > 0
        if (head.length() > 0) {
            if (head.endsWith(SEPARATOR_AFTER_DRIVE)) {
                info.drive = head.substring(0, head.length() - SEPARATOR_AFTER_DRIVE.length());
            }
        } else {
            info.drive = StringUtil.STRING_EMPTY;
        }
        int dirStartIndex = info.drive == null ? 0 : 1;
        for (int i = dirStartIndex; i < segs.length - 1; i++) {
            info.dirs.add(segs[i]);
        }
        info.setFilename(segs[segs.length - 1]);
        return info;
    }

    public static final PathInfo[] parseMultiple(String... pathes) {
        if (pathes == null) {
            return null;
        }
        PathInfo[] results = new PathInfo[pathes.length];
        for (int i = 0; i < pathes.length; i++) {
            results[i] = parse(pathes[i]);
        }
        return results;
    }

    /**
     * @param concat {@code true} to run in concat mode, {@code false} to run under relative mode
     * @param pathes
     * @return
     */
    public static final PathInfo combine(boolean concat, PathInfo... pathes) {
        if (pathes == null) {
            return null;
        }
        PathInfo result = null;
        for (int i = pathes.length - 1; i >= 0; i--) {
            PathInfo path = pathes[i];
            if (path == null) {
                continue;
            }
            if (result == null) {
                result = path.clone();
                continue;
            } else if (result.isAbsolute()) {
                break;
            }
            result.drive = path.drive;
            result.dirs.addAll(0, path.dirs);
            if (concat && path.file != null) {
                result.dirs.add(path.dirs.size(), path.getFilename());
            }
        }
        return result;
    }


    public static final PathInfo combine(boolean concat, String... pathes) {
        return combine(concat, parseMultiple(pathes));
    }

    public static final String combine(boolean concat, boolean shouldNormalize, String... pathes) {
        PathInfo result = combine(concat, pathes);
        if (result == null) {
            return null;
        }
        if (shouldNormalize) {
            result.normalize();
        }
        return result.getFullPath();
    }

    public static class PathInfo implements Cloneable {
        /**
         * Directory segments without the last (filename) part, never be {@code null}.
         */
        private List<String> dirs = new ArrayList<>();
        /**
         * Filename (the last) part without ext, {@code null} means this is a directory (corresponding to path end with {@link #SEPARATOR_IN_PATH}, or just {@link #SPECIAL_FILE_CURRENT_DIR}/{@link #SPECIAL_FILE_PARENT_DIR}).
         */
        private String file;
        /**
         * {@code null} means no ext (neither for {@link #SEPARATOR_BEFORE_EXT}), empty string means empty ext but has {@link #SEPARATOR_BEFORE_EXT}, non-empty string for normal ext (not including {@link #SEPARATOR_BEFORE_EXT}).
         */
        private String ext;
        /**
         * {@code null} means relative, otherwise absolute, while empty string means no specified drive, non-empty string used in windows style (without {@link #SEPARATOR_AFTER_DRIVE})
         */
        private String drive;

        public PathInfo clone() {
            PathInfo info = null;
            try {
                info = (PathInfo)super.clone();
            } catch (CloneNotSupportedException e) {
                ExceptionUtil.rethrowAsRuntime(e); // should not happen
            }
            return info;
        }

        /**
         * @return Including {@link #SEPARATOR_BEFORE_EXT}
         */
        public String getExtension() {
            return ext == null ? StringUtil.STRING_EMPTY : SEPARATOR_BEFORE_EXT + ext;
        }

        public String getFilenameWithoutExtension() {
            return SyntaxUtil.nvl(file, StringUtil.STRING_EMPTY);
        }

        public String getFilename() {
            return getFilenameWithoutExtension() + getExtension();
        }

        public String getDrive() {
            return StringUtil.isNullOrEmpty(drive) ? StringUtil.STRING_EMPTY : drive + SEPARATOR_AFTER_DRIVE;
        }

        public boolean isAbsolute() {
            return drive != null;
        }

        public String getDirectory() {
            return getDrive() + (isAbsolute() ? SEPARATOR_IN_PATH : StringUtil.STRING_EMPTY) + StringUtil.join(true, SEPARATOR_IN_PATH, dirs);
        }

        public String getFullPath() {
            return getDirectory() + SEPARATOR_IN_PATH + getFilename();
        }

        public void normalize() {
            int pending = 0;
            for (int i = dirs.size() - 1; i >= 0; i--) {
                String dir = dirs.get(i);
                if (StringUtil.isNullOrEmpty(dir) || SPECIAL_FILE_CURRENT_DIR.equals(dir)) {
                    dirs.remove(i);
                    continue;
                }
                if (SPECIAL_FILE_PARENT_DIR.equals(dir)) {
                    pending++;
                    continue;
                }
                if (pending > 0) {
                    dirs.remove(i);
                    dirs.remove(i);
                    continue;
                }
            }
        }

        public PathInfo setExtension(String ext, boolean mayIncludeSeparator) {
            if (mayIncludeSeparator && ext != null && ext.startsWith(SEPARATOR_BEFORE_EXT)) {
                ext = ext.substring(SEPARATOR_BEFORE_EXT.length());
            }
            this.ext = ext;
            return this;
        }

        public PathInfo setFilename(String f) {
            if (StringUtil.isNullOrEmpty(f)) {
                file = null;
            } else if (SPECIAL_FILE_CURRENT_DIR.equals(f) || SPECIAL_FILE_PARENT_DIR.equals(f)) {
                dirs.add(f);
            } else {
                int pos = f.lastIndexOf(SEPARATOR_BEFORE_EXT);
                if (pos < 0) {
                    file = f;
                } else {
                    file = f.substring(0, pos);
                    setExtension(f.substring(pos + 1), false);
                }
            }
            return this;
        }

        /**
         * @param f Extension will be cleared if {@code null}, {@link #SPECIAL_FILE_CURRENT_DIR} and {@link #SPECIAL_FILE_PARENT_DIR} get passed.
         * @return
         */
        public PathInfo setFilenameWithoutExtension(String f) {
            if (StringUtil.isNullOrEmpty(f)) {
                file = null;
                ext = null;
            } else if (SPECIAL_FILE_CURRENT_DIR.equals(f) || SPECIAL_FILE_PARENT_DIR.equals(f)) {
                dirs.add(f);
                ext = null;
            } else {
                file = f;
            }
            return this;
        }

        @Override
        public String toString() {
            return getFullPath();
        }
    }
}
