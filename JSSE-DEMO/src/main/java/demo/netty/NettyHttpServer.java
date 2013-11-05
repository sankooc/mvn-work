package demo.netty;

import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.handler.codec.http.HttpHeaders.getHost;
import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.context.SSLContextBuilder;

public class NettyHttpServer implements ChannelPipelineFactory {
	private int port = 8080;
	static Logger logger = LoggerFactory.getLogger(NettyHttpServer.class);
	SSLEngine engine;

	public NettyHttpServer(SSLContextBuilder builder,boolean bidirection) {
		if (null == builder) {
			return;
		}
		engine = builder.getSSLContext().createSSLEngine();
		engine.setUseClientMode(false);
		engine.setNeedClientAuth(bidirection);
	}

	public void startup() {
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(this);
		bootstrap.bind(new InetSocketAddress(port));
	}

	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = pipeline();
		if (null != engine) {
			pipeline.addLast("ssl", new SslHandler(engine));
		}
		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("deflater", new HttpContentCompressor());
		pipeline.addLast("handler", new ServerHandler());
		return pipeline;
	}
}
class ServerHandler extends SimpleChannelUpstreamHandler{
	    private HttpRequest request;
	    private boolean readingChunks;
	    private final StringBuilder buf = new StringBuilder();
	    @Override
	    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
	        if (!readingChunks) {
	            HttpRequest request = this.request = (HttpRequest) e.getMessage();

	            if (is100ContinueExpected(request)) {
	                send100Continue(e);
	            }

	            buf.setLength(0);
	            buf.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
	            buf.append("===================================\r\n");

	            buf.append("VERSION: " + request.getProtocolVersion() + "\r\n");
	            buf.append("HOSTNAME: " + getHost(request, "unknown") + "\r\n");
	            buf.append("REQUEST_URI: " + request.getUri() + "\r\n\r\n");

	            for (Map.Entry<String, String> h: request.getHeaders()) {
	                buf.append("HEADER: " + h.getKey() + " = " + h.getValue() + "\r\n");
	            }
	            buf.append("\r\n");

	            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
	            Map<String, List<String>> params = queryStringDecoder.getParameters();
	            if (!params.isEmpty()) {
	                for (Entry<String, List<String>> p: params.entrySet()) {
	                    String key = p.getKey();
	                    List<String> vals = p.getValue();
	                    for (String val : vals) {
	                        buf.append("PARAM: " + key + " = " + val + "\r\n");
	                    }
	                }
	                buf.append("\r\n");
	            }

	            if (request.isChunked()) {
	                readingChunks = true;
	            } else {
	                ChannelBuffer content = request.getContent();
	                if (content.readable()) {
	                    buf.append("CONTENT: " + content.toString(CharsetUtil.UTF_8) + "\r\n");
	                }
	                System.out.println(buf.toString());
	                writeResponse(e);
	            }
	        } else {
	            HttpChunk chunk = (HttpChunk) e.getMessage();
	            if (chunk.isLast()) {
	                readingChunks = false;
	                buf.append("END OF CONTENT\r\n");

	                HttpChunkTrailer trailer = (HttpChunkTrailer) chunk;
	                if (!trailer.getHeaderNames().isEmpty()) {
	                    buf.append("\r\n");
	                    for (String name: trailer.getHeaderNames()) {
	                        for (String value: trailer.getHeaders(name)) {
	                            buf.append("TRAILING HEADER: " + name + " = " + value + "\r\n");
	                        }
	                    }
	                    buf.append("\r\n");
	                }

	                writeResponse(e);
	            } else {
	                buf.append("CHUNK: " + chunk.getContent().toString(CharsetUtil.UTF_8) + "\r\n");
	            }
	        }
	    }

//	    @Override
//	    public void channelConnected(
//	            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//
//	        // Get the SslHandler in the current pipeline.
//	        // We added it in SecureChatPipelineFactory.
//	        final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);
//
//	        // Get notified when SSL handshake is done.
//	        ChannelFuture handshakeFuture = sslHandler.handshake();
//	        handshakeFuture.addListener(new Greeter(sslHandler));
//	    }
	    
	    private void writeResponse(MessageEvent e) {
	        // Decide whether to close the connection or not.
	        boolean keepAlive = isKeepAlive(request);

	        // Build the response object.
	        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
	        response.setContent(ChannelBuffers.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));
	        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");

	        if (keepAlive) {
	            // Add 'Content-Length' header only for a keep-alive connection.
	            response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
	            // Add keep alive header as per http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
	            response.setHeader(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
	        }
	        System.out.println(request.getUri());
	        // Encode the cookie.
	        String cookieString = request.getHeader(COOKIE);
	        if (cookieString != null) {
	            CookieDecoder cookieDecoder = new CookieDecoder();
	            Set<Cookie> cookies = cookieDecoder.decode(cookieString);
	            if (!cookies.isEmpty()) {
	                // Reset the cookies if necessary.
	                CookieEncoder cookieEncoder = new CookieEncoder(true);
	                for (Cookie cookie : cookies) {
	                    cookieEncoder.addCookie(cookie);
	                }
	                response.addHeader(SET_COOKIE, cookieEncoder.encode());
	            }
	        }

	        // Write the response.
	        ChannelFuture future = e.getChannel().write(response);

	        // Close the non-keep-alive connection after the write operation is done.
	        if (!keepAlive) {
	            future.addListener(ChannelFutureListener.CLOSE);
	        }
	    }

	    private void send100Continue(MessageEvent e) {
	        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
	        e.getChannel().write(response);
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
	            throws Exception {
	        e.getCause().printStackTrace();
	        e.getChannel().close();
	    }
//	    class Greeter implements ChannelFutureListener {
//
//	        private final SslHandler sslHandler;
//
//	        Greeter(SslHandler sslHandler) {
//	            this.sslHandler = sslHandler;
//	        }
//
//	        public void operationComplete(ChannelFuture future) throws Exception {
//	            if (future.isSuccess()) {
//	            } else {
//	                future.getChannel().close();
//	            }
//	        }
//	    }
}