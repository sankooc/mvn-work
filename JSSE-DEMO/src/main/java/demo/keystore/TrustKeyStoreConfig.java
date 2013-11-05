package demo.keystore;

import java.io.InputStream;

public interface TrustKeyStoreConfig {
	InputStream getTrustStoreSource();
	char[] getTrustPassword();
}
