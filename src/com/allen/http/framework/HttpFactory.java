package com.allen.http.framework;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.net.Proxy;
import android.text.TextUtils;
import android.util.Log;

/**
 * A commmon httpClient factory,override the  {@link org.apache.http.impl.client.DefaultHttpClient}
 * has a timeout expired login authentication.
 * Support http/https for the method of get and post.
 */
public class HttpFactory {
	/**the max requests*/
	private static final int DEFAULT_MAX_CONNECTIONS = 30;
	private static final int DEFAULT_SOCKET_TIMEOUT = 20 * 1000;
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;

	private static DefaultHttpClient sHttpClient;
	static {
		final HttpParams httpParams = new BasicHttpParams();

		ConnManagerParams.setTimeout(httpParams, 1000);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(10));
		ConnManagerParams.setMaxTotalConnections(httpParams,
				DEFAULT_MAX_CONNECTIONS);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
		HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
		HttpClientParams.setRedirecting(httpParams, false);
		HttpProtocolParams.setUserAgent(httpParams, "Android client");
		HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
		HttpConnectionParams.setConnectionTimeout(httpParams,
				DEFAULT_SOCKET_TIMEOUT);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams,
				DEFAULT_SOCKET_BUFFER_SIZE);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			schemeRegistry.register(new Scheme("https", sf, 443));
		} catch (Exception ex) {
			// do nothing, just keep not crash
		}

		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				httpParams, schemeRegistry);
		sHttpClient = new DefaultHttpClient(manager, httpParams);
	}

	private HttpFactory() {
		// TODO Auto-generated constructor stub
	}

	public static HttpResponse execute(HttpHead head) throws IOException {
		return sHttpClient.execute(head);
	}

	public static HttpResponse execute(HttpHost host, HttpGet get)
			throws IOException {
		return sHttpClient.execute(host, get);
	}

	public static HttpResponse execute(Context context, HttpGet get)
			throws IOException {
		if (!IOUtils.isWifiAvailable(context) && isWapNetwork()) {
			setWapProxy();
			return sHttpClient.execute(get);
		}

		final HttpHost host = (HttpHost) sHttpClient.getParams().getParameter(
				ConnRouteParams.DEFAULT_PROXY);
		if (host != null) {
			sHttpClient.getParams().removeParameter(
					ConnRouteParams.DEFAULT_PROXY);
		}

		return sHttpClient.execute(get);
	}

	public static HttpResponse execute(Context context, HttpPost post)
			throws IOException {
		if (!IOUtils.isWifiAvailable(context) && isWapNetwork()) {
			setWapProxy();
			return sHttpClient.execute(post);
		}

		final HttpHost host = (HttpHost) sHttpClient.getParams().getParameter(
				ConnRouteParams.DEFAULT_PROXY);
		if (host != null) {
			sHttpClient.getParams().removeParameter(
					ConnRouteParams.DEFAULT_PROXY);
		}
		return sHttpClient.execute(post);
	}

	private static boolean isWapNetwork() {
		final String proxyHost = android.net.Proxy.getDefaultHost();
		return !TextUtils.isEmpty(proxyHost);
	}

	private static void setWapProxy() {
		final HttpHost host = (HttpHost) sHttpClient.getParams().getParameter(
				ConnRouteParams.DEFAULT_PROXY);
		if (host == null) {
			final String host1 = Proxy.getDefaultHost();
			int port = Proxy.getDefaultPort();
			HttpHost httpHost = new HttpHost(host1, port);
			sHttpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
					httpHost);
		}
	}

	private static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	/**
	 * send a get Request to http server.
	 * @param context 
	 * @param url 
	 * @return String
	 */
	public static String getHttpData(Context context, String url) {
		if (url == null || "".equals(url)) {
			return null;
		}
		InputStream in = null;
		HttpEntity entity = null;
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse res = HttpFactory.execute(context, get);
			final int statusCode = res.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				entity = res.getEntity();
				if (entity != null) {
					in = entity.getContent();
					String str = IOUtils.stream2String(in);
					// System.out.println("********str*******" + str);
					return str;
				} else
					get.abort();
			}

		} catch (IOException e) {
			Log.e("NetHelper", "__getHttpData__" + e.toString()
					+ "_____");
		}
		return "";
	}

	/**
	 * send a post Request to http server.
	 * @param context 
	 * @param url 
	 * @param the url parameters,if not or null
	 * @return String
	 */
	public static String postHttpData(Context context, String url,
			Map<String, String> params) {
		final HttpPost post = new HttpPost(url);
		InputStream in = null;
		HttpEntity entity = null;
		try {
			// add the url parameters
			List<NameValuePair> pair = new ArrayList<NameValuePair>();
			if (params != null && !params.isEmpty()) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					pair.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
			}
			//setting the url encodeing
			post.setEntity(new UrlEncodedFormEntity(pair, "UTF-8"));
			final HttpResponse response = HttpFactory.execute(context, post);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				entity = response.getEntity();
				if (entity != null) {
					in = entity.getContent();
					return IOUtils.stream2String(in);
				}
			} else {
				post.abort();
			}
			return null;
		} catch (Exception ex) {
			post.abort();
			Log.e("NetHelper", "__postHttpData__" + ex.toString()
					+ "_____");
		} finally {
			if (entity != null) {
				try {
					entity.consumeContent();
				} catch (IOException e) {
					Log.e("NetHelper", "__postHttpData__" + e.toString()
							+ "_____");
				}
			}
			IOUtils.closeStream(in);
		}
		return "";
	}
}
