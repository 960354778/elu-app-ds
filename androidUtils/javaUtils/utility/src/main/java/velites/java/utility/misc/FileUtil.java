package velites.java.utility.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;

public class FileUtil {
    private FileUtil() {}

    public static File copyFile(String src, String dest) throws IOException {
        return copyFile(new File(src), new File(dest));
    }

    public static File copyFile(File srcFile, File dstFile) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        if (srcFile.exists()) {
            ensureDir(dstFile, true);
            if (dstFile.exists()) {
                dstFile.delete();
            }
            try {
                dstFile.createNewFile();
                in = new FileInputStream(srcFile);
                out = new FileOutputStream(dstFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                if(out != null) {
                    out.flush();
                }
                return dstFile;
            } finally {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }
        }
        return null;
    }

    public static boolean ensureDir(File f, boolean targetToParent) {
        if (targetToParent) {
            f = f.getParentFile();
        }
        boolean created = false;
        if (!f.exists()) {
            created = f.mkdirs();
        }
        return created;
    }

    public static File ensureDir(String path, boolean targetToParent) {
        if (StringUtil.isNullOrSpace(path)) {
            return null;
        }
        File f = new File(path);
        ensureDir(f, targetToParent);
        return f;
    }

    public static File prepareFileForCreation(String path) {
        File f = ensureDir(path, true);
        if (f.exists()){
            deleteFile(f);
        }
        return f;
    }

    public static File ensureFileNewlyCreated(String path) throws IOException {
        File f = prepareFileForCreation(path);
        f.createNewFile();
        return f;
    }

    public static File ensureFileExists(File f) throws IOException {
        if (!f.exists()) {
            ensureDir(f, true);
            f.createNewFile();
        }
        return f;
    }

    public static boolean isExistsForFile(String path){
        if(StringUtil.isNullOrEmpty(path))
            return false;
        File file = new File(path);
        return file.exists();
    }

    public static void deleteFile(String file) {
        deleteFile(new File(file));
    }

    public static void deleteFile(File file) {
        if (deleteFileRecursive(file)) {
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, FileUtil.class, "deleted file %s", file));
        }
    }

    private static boolean deleteFileRecursive(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] childFiles = file.listFiles();
                if (childFiles != null) {
                    for (int i = 0; i < childFiles.length; i++) {
                        deleteFile(childFiles[i]);
                    }
                }
            }
            return file.delete();
        }
        return false;
    }

    public static void moveFile(String src, String dest) throws IOException {
        copyFile(src, dest);
        deleteFile(src);
    }

    public static void moveFile(File src, File dest) throws IOException {
        copyFile(src, dest);
        deleteFile(src);
    }

    public static long getFileSize(File f) throws Exception {
        long size = 0;
        f.mkdirs();
        File[] flist = f.listFiles();
        if(flist != null){
            for (File ff : flist) {
                if (ff != null) {
                    if (ff.isDirectory()) {
                        size = size + getFileSize(ff);
                    } else {
                        size = size + ff.length();
                    }
                }
            }
        }
        return size;
    }
}
