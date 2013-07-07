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
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.util.Log;

/**
 * A simple httpClient factory,override the  {@link org.apache.http.impl.client.DefaultHttpClient}
 * Support get and post.
 */
public class HttpSimpleFactory {

	private static DefaultHttpClient sHttpClient;
	static {
		final HttpParams httpParams = new BasicHttpParams();

		// Set the connection timeout and Socket timeout, as well as cache size Socket
		HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 10 * 1000);
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);

		// Set redirection defaults to true
		HttpClientParams.setRedirecting(httpParams, true);
		// set user agent
		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
		HttpProtocolParams.setUserAgent(httpParams, userAgent);
		sHttpClient = new DefaultHttpClient(httpParams);
	}

	private HttpSimpleFactory() {
	}

	public static HttpResponse execute(Context context, HttpGet get)
			throws IOException {
		return sHttpClient.execute(get);
	}

	public static HttpResponse execute(Context context, HttpPost post)
			throws IOException {
		return sHttpClient.execute(post);
	}
}
