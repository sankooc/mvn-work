package demo;


import demo.context.SSLContextBuilder;
import demo.hc.ApacheHttpClient;
import demo.netty.NettyHttpServer;

public class NettyServiceDemo {
	public static void main(String[] args){
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.addKeyStore(KeyStoreProvider.getServerKeyConfig());
//		builder.addKeyStore(KeyStoreProvider.getClientConfig());
//		builder.addTrustKeyStore(KeyStoreProvider.getClientTrustConfig());
		NettyHttpServer server = new NettyHttpServer(builder,false);
		server.startup();
		System.out.println("server started");
		builder = new SSLContextBuilder();
		builder.addTrustKeyStore(KeyStoreProvider.getServerTrustConfig());
//		builder.addKeyStore(KeyStoreProvider.getClientConfig());
//		builder.addTrustStore(new TrustManager[]{builder.TRUST_MANAGER});
		ApacheHttpClient client = new ApacheHttpClient(builder);
		client.execute();
		System.out.println("do post request");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
