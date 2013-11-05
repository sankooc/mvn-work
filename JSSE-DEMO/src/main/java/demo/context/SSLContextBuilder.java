package demo.context;

import java.lang.reflect.Array;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.keystore.KeyStoreConfig;
import demo.keystore.TrustKeyStoreConfig;

public class SSLContextBuilder {

	private static final String KEYSTORE_TYPE = "JKS";
	private static final String PROTOCOL = "TLS";
	private List<KeyManager> klist = new LinkedList<KeyManager>();
	private List<TrustManager> tlist = new LinkedList<TrustManager>();
	private Logger logger = LoggerFactory.getLogger(SSLContextBuilder.class);
	public final TrustManager TRUST_MANAGER = new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(
                X509Certificate[] chain, String authType) throws CertificateException {
            // Always trust - it is an example.
            // You should do something in the real world.
            // You will reach here only if you enabled client certificate auth,
            // as described in SecureChatSslContextFactory.
            System.err.println(
                    "UNKNOWN CLIENT CERTIFICATE: " + chain[0].getSubjectDN());
        }

        public void checkServerTrusted(
                X509Certificate[] chain, String authType) throws CertificateException {
            // Always trust - it is an example.
            // You should do something in the real world.
            System.err.println(
                    "UNKNOWN SERVER CERTIFICATE: " + chain[0].getSubjectDN());
        }
    };
	public void addTrustKeyStore(TrustKeyStoreConfig config) {
		try {
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
			ks.load(config.getTrustStoreSource(), config.getTrustPassword());
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance("SunX509");
			tmf.init(ks);
			for (TrustManager tm : tmf.getTrustManagers()) {
				tlist.add(tm);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void addKeyStore(KeyStoreConfig config) {
		try {
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
			ks.load(config.getKeyStoreSource(), config.getKeyStorePassword());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, config.getKeyStoreCertificatePassword());
			addKeyStore(kmf.getKeyManagers());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void addTrustStore(TrustManager[] tms){
		for (TrustManager tm : tms) {
			tlist.add(tm);
		}
	}
	public void addKeyStore(KeyManager[] kms){
		for (KeyManager km : kms) {
			klist.add(km);
		}
	}
	@SuppressWarnings("unchecked")
	private <T> T[] getManager(Class<T> clz, List<T> list) {
		if (null == list || list.isEmpty()) {
			return null;
		}
		T[] t = (T[]) Array.newInstance(clz, list.size());
		list.toArray(t);
		return t;
	}

	public SSLContext getSSLContext() {
		try {
			SSLContext context = SSLContext.getInstance(PROTOCOL);
			context.init(getManager(KeyManager.class, klist), getManager(TrustManager.class, tlist), null);
			return context;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
