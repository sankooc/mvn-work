package demo.keystore;

import java.io.InputStream;

public interface KeyStoreConfig {
	InputStream getKeyStoreSource();
	char[] getKeyStorePassword();
	char[] getKeyStoreCertificatePassword();
}
