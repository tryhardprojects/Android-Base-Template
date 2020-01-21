package dean.tryhard.project.baseproject.api.core;

import android.content.Context;
import android.os.Build;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class NetworkParam {

    private final String baseUrl;
    private SSLSocketFactory sslSocketFactory;
    private X509TrustManager trustManager;

    public NetworkParam(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public X509TrustManager getTrustManager() {
        return trustManager;
    }


    public NetworkParam setSSLContext(Context context1, int[] resIds, boolean isSSLPinning) {

        try {
            if (isSSLPinning && resIds.length > 0) {
                // Load CAs from an InputStream
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);

                for (int i = 0; i < resIds.length; i++) {
                    InputStream caInput = context1.getResources().openRawResource(resIds[i]);
                    Certificate ca;
                    try {
                        ca = cf.generateCertificate(caInput);
//                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                    } finally {
                        safeClose(caInput);
                    }
                    keyStore.setCertificateEntry("ca" + i, ca);
                }

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                TrustManager[] trustManagers = tmf.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:"
                            + Arrays.toString(trustManagers));
                }
                X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

                // Create an SSLContext that uses our TrustManager
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    // 手機在4.4以下的版本, TLSv1.2還是要打開.
                    this.sslSocketFactory = new TLSSocketFactory(context.getSocketFactory());
                } else {
                    this.sslSocketFactory = context.getSocketFactory();
                }

                this.trustManager = trustManager;

            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                // 不用pinning, 但是手機在4.4以下的版本, TLSv1.2還是要打開.
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:"
                            + Arrays.toString(trustManagers));
                }
                X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[] { trustManager }, null);

                this.sslSocketFactory = new TLSSocketFactory(sslContext.getSocketFactory());
                this.trustManager = trustManager;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    private void safeClose(Closeable fis) {
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                //
            }
        }
    }
}
