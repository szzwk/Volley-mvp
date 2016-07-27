package com.pt.network;

import com.pt.network.context.AppContext;
import com.pt.network.utils.Util;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author: yorkzhang
 * @time: 16/7/18 15:45
 * @email: xtcqw13@126.com
 * @note:
 */
public class SSLAuthentication {

    private static final String[] CER = {"GeoTrust SSL CA1.cer", "GeoTrust SSL CA2.cer"};
    private static SSLContext sslContext;
    private final static int timeout = 20000;

    private static SSLContext getSslContext(String url) {
        if (sslContext == null) {
            synchronized (SSLAuthentication.class) {
                if (sslContext == null) {
                    sslContext = createSSLContext();
                }
            }
        }
        return sslContext;
    }

    private static KeyStore createKeyStoreIfNotExist() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore sslKeyStore = null;
        synchronized (SSLAuthentication.class) {
            InputStream is = null;
            try {
                String keyStoreType = KeyStore.getDefaultType();
                sslKeyStore = KeyStore.getInstance(keyStoreType);
                sslKeyStore.load(null, null);
                for (String cer : CER) {  //将assets目录下面的两个公钥证书导入证书库sslKeyStore
                    is = AppContext.getApplicationContext().getAssets().open(cer);
                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    Certificate certificate = certificateFactory.generateCertificate(is);
                    sslKeyStore.setCertificateEntry(cer, certificate);
                }
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        printException(e);
                    }
                }
            }
        }
        return sslKeyStore;
    }

    private static SSLContext createSSLContext() {
        try {
            KeyStore keyStore = createKeyStoreIfNotExist();
            if (keyStore == null) {
                return null;
            }
            String tmfAlgotithm = TrustManagerFactory.getDefaultAlgorithm(); //默认算法
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgotithm);
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            return sslContext;
        } catch (NoSuchAlgorithmException e) {
            printException(e);
        } catch (KeyStoreException e) {
            printException(e);
        } catch (KeyManagementException e) {
            printException(e);
        } catch (CertificateException e) {
            printException(e);
        } catch (IOException e) {
            printException(e);
        }
        return null;
    }

    private static void printException(Exception e) {
        e.printStackTrace();
    }

    /**
     * 设置SSL证书认证
     *
     * @param url        the request url
     * @param connection httpurlconnect object
     */
    public static void setSSLAuthenticationIfNeeded(String url, HttpURLConnection connection) {
        if (Util.isTestEnvironment()) {
            HTTPSTrustManager.allowAllSSL();
            return;
        }
        if (!isHttpsDomain(url)) {
            return;
        }
        try {
            SSLContext sslContext = getSslContext(url);
            if (sslContext == null) {
                return;
            }
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param urlStr url of this request
     * @return true if the url is https domain,otherwise is false
     */
    public static boolean isHttpsDomain(String urlStr) {
        String lower = urlStr.toLowerCase();
        return lower.startsWith("https");
    }

    public static class HTTPSTrustManager implements X509TrustManager {

        static TrustManager[] trustManagers;
        private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};

        @Override
        public void checkClientTrusted(
                X509Certificate[] x509Certificates, String s)
                throws java.security.cert.CertificateException {
        }

        @Override
        public void checkServerTrusted(
                X509Certificate[] x509Certificates, String s)
                throws java.security.cert.CertificateException {
        }

        public boolean isClientTrusted(X509Certificate[] chain) {
            return true;
        }

        public boolean isServerTrusted(X509Certificate[] chain) {
            return true;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return _AcceptedIssuers;
        }

        public static void allowAllSSL() {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

            });

            SSLContext context = null;
            if (trustManagers == null) {
                trustManagers = new TrustManager[]{new HTTPSTrustManager()};
            }

            try {
                context = SSLContext.getInstance("TLS");    //不验证证书
                context.init(null, trustManagers, new SecureRandom());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            if (context != null) {
                HttpsURLConnection.setDefaultSSLSocketFactory(context
                        .getSocketFactory());
            }
        }
    }

    public static class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {

                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port,
                    autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
}
