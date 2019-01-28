package velites.java.utility.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Created by luohongzhen on 03/12/2017.
 */

public class EncryptUtil {
    public static String MD5(String content) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(content.getBytes("UTF-8"));
            byte[] encryption = md5.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(encryption[i] & 255).length() == 1) {
                    sb.append("0").append(Integer.toHexString(encryption[i] & 255));
                } else {
                    sb.append(Integer.toHexString(encryption[i] & 255));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String MD5Lower(String content) {
        String str = MD5(content);
        if (str != null) {
            str = str.toLowerCase();
        }
        return str;
    }

    public static String computeFileSha1(File f) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead);
            }
            byte [] sha1Bytes = digest.digest();
            return convertHashToString(sha1Bytes);
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) { }
            }
        }
    }

    public static String computeFileHashByDefault(File f) {
        return computeFileSha1(f);
    }

    private static String convertHashToString(byte[] hashBytes) {
        String returnVal = "";
        for (int i = 0; i < hashBytes.length; i++) {
            returnVal += Integer.toString(( hashBytes[i] & 0xff) + 0x100, 16).substring(1);
        }
        return returnVal.toLowerCase();
    }
}
