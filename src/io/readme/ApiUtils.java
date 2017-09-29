package io.readme;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class ApiUtils {
  //api.readme.build is signed with a Let's Encrypt certificate which is not out
  //of the box supported by Java. This custom TrustManager fixes this.
  //adapted from: https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/CustomTrust.java
  public static OkHttpClient createClient() {
    X509TrustManager trustManager;
    SSLSocketFactory sslSocketFactory;

    try {
      trustManager = trustManagerForCertificates(new BufferedInputStream(Api.class.getResourceAsStream("DSTRootCAX3.pem")));
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new TrustManager[] { trustManager }, null);
      sslSocketFactory = sslContext.getSocketFactory();
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }

    return new OkHttpClient.Builder()
                           .sslSocketFactory(sslSocketFactory, trustManager)
                           .build();
  }

  /**
   * Returns a trust manager that trusts {@code certificates} and none other. HTTPS services whose
   * certificates have not been signed by these certificates will fail with a {@code
   * SSLHandshakeException}.
   *
   * <p>This can be used to replace the host platform's built-in trusted certificates with a custom
   * set. This is useful in development where certificate authority-trusted certificates aren't
   * available. Or in production, to avoid reliance on third-party certificate authorities.
   *
   * <p>See also {@link CertificatePinner}, which can limit trusted certificates while still using
   * the host platform's built-in trust store.
   *
   * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
   *
   * <p>Relying on your own trusted certificates limits your server team's ability to update their
   * TLS certificates. By installing a specific set of trusted certificates, you take on additional
   * operational complexity and limit your ability to migrate between certificate authorities. Do
   * not use custom trusted certificates in production without the blessing of your server's TLS
   * administrator.
   */
  private static X509TrustManager trustManagerForCertificates(InputStream in) throws GeneralSecurityException {
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
    if (certificates.isEmpty()) {
      throw new IllegalArgumentException("expected non-empty set of trusted certificates");
    }

    // Put the certificates a key store.
    char[] password = "password".toCharArray(); // Any password will work.
    KeyStore keyStore = newEmptyKeyStore(password);
    int index = 0;
    for (Certificate certificate : certificates) {
      String certificateAlias = Integer.toString(index++);
      keyStore.setCertificateEntry(certificateAlias, certificate);
    }

    // Use it to build an X509 trust manager.
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(keyStore, password);
    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(keyStore);
    TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
    if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
      throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
    }
    return (X509TrustManager) trustManagers[0];
  }

  private static KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
    try {
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      InputStream in = null; // By convention, 'null' creates an empty key store.
      keyStore.load(in, password);
      return keyStore;
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }

  public static Properties loadProperties(String filename) throws IOException {
    try (InputStream stream = Files.newInputStream(Paths.get(filename))) {
      Properties config = new Properties();
      config.load(stream);
      return config;
    }
  }
}
