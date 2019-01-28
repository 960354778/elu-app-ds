package velites.android.utility.misc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import velites.java.utility.misc.ExceptionUtil;

public class NetHelper {

    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info != null && info.isAvailable();
        }
        return false;
    }

    public static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }

    public static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static SSLSocketFactory createTrustAllSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null,  new TrustManager[] { new TrustAllCerts() }, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ex) {
            ExceptionUtil.swallowThrowable(ex);
        }
        return ssfFactory;
    }
}
