package demo;

import java.io.InputStream;

import demo.keystore.KeyStoreConfig;
import demo.keystore.TrustKeyStoreConfig;

public class KeyStoreProvider {
	public static KeyStoreConfig getClientConfig(){
		return new KeyStoreConfig(){

			public InputStream getKeyStoreSource() {
				return KeyStoreProvider.class.getClassLoader().getResourceAsStream("client/kclient.keystore");
			}

			public char[] getKeyStorePassword() {
				return "sankooc".toCharArray();
			}

			public char[] getKeyStoreCertificatePassword() {
				return "123456".toCharArray();
			}			
		};
	}
	public static TrustKeyStoreConfig getClientTrustConfig(){
		return new TrustKeyStoreConfig() {
			
			public InputStream getTrustStoreSource() {
				return KeyStoreProvider.class.getClassLoader().getResourceAsStream("client/tclient.keystore");
			}
			
			public char[] getTrustPassword() {
				return "sankooc".toCharArray();
			}
		};
	}
	
	public static TrustKeyStoreConfig getServerTrustConfig(){
		return new TrustKeyStoreConfig() {
			
			public InputStream getTrustStoreSource() {
				return KeyStoreProvider.class.getClassLoader().getResourceAsStream("server/tserver.keystore");
			}
			
			public char[] getTrustPassword() {
				return "sankooc".toCharArray();
			}
		};
	}
	public static KeyStoreConfig getServerKeyConfig(){
		return new KeyStoreConfig(){

			public InputStream getKeyStoreSource() {
				return KeyStoreProvider.class.getClassLoader().getResourceAsStream("server/kserver.keystore");
			}

			public char[] getKeyStorePassword() {
				return "sankooc".toCharArray();
			}

			public char[] getKeyStoreCertificatePassword() {
				return "123456".toCharArray();
			}			
		};
	}
}
