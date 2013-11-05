package demo.hc;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import demo.context.SSLContextBuilder;

public class ApacheHttpClient {
	SSLContextBuilder builder;
	DefaultHttpClient client;
	private static final int DEFAULT_HTTP_PORT = 80;
	private static final int DEFAILT_HTTPS_PORT = 443;
	public ApacheHttpClient(SSLContextBuilder builder){
		this.builder = builder;
		SSLContext context =builder.getSSLContext();
		SSLSocketFactory socketFactory = new SSLSocketFactory(context,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		ClientConnectionManager ccm = new BasicClientConnectionManager();
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		ccm.getSchemeRegistry().register(new Scheme("http", DEFAULT_HTTP_PORT,PlainSocketFactory.getSocketFactory()));
		ccm.getSchemeRegistry().register(new Scheme("https", DEFAILT_HTTPS_PORT, socketFactory));
		client = new DefaultHttpClient(ccm,params);
	}
	
	public void execute(){
		try {
			HttpPost post = new HttpPost("https://127.0.0.1:8080/test");
			HttpEntity entity  = new StringEntity("啦啦啦 德玛西亚",Charset.forName("UTF-8"));
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			entity = response.getEntity();
			System.out.println("----------------------------------------");
			System.out.println(response.getStatusLine());
			if (entity != null) {
				System.out.println("Response content length: "
						+ entity.getContentLength());
			}
			EntityUtils.consume(entity);
			post.abort();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
