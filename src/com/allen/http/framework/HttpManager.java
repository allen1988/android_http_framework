package com.allen.http.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * start http request from this,it extends thread
 * 
 * 
 */
public class HttpManager implements Request {

	private Context context;
	private static final String TAG = HttpManager.class.getSimpleName();

	public HttpManager(Context context) {
		this.context = context;
	}

	/**
	 * get request
	 * 
	 * @param url
	 * @param handler
	 *            see the abstract class {@link TaskHandler},you can use the
	 *            subclass eg. {@link StringTaskHandler} and so on}
	 */
	public void request(String url, TaskHandler handler) {
		if (context != null) {
			synchronized (this) {
				new HttpTask(context, url, 0, null, handler).start();
			}

		}
	}

	/**
	 * post request
	 * 
	 * @param url
	 * @param params
	 *            url parameters,if not use null
	 * @param handler
	 *            see the abstract class {@link TaskHandler},you can use the
	 *            subclass eg. {@link StringTaskHandler} and so on}
	 */
	public void request(String url, Map<String, String> params,
			TaskHandler handler) {
		if (context != null) {
			synchronized (this) {
				new HttpTask(context, url, 1, params, handler).start();
			}

		}
	}

	/**task */
	private static class HttpTask extends Thread {

		Context context;
		String url;
		/** 0 is get,1 is post */
		int type = 0;
		TaskHandler handler;
		Map<String, String> params;

		
		public HttpTask(Context context, String url, int type,
				Map<String, String> params, TaskHandler handler) {
			this.context = context;
			this.url = url;
			this.type = type;
			this.handler = handler;
			this.params = params;
		}

		@Override
		public void run() {
			doInBackground();
		}

		private void onNetError() {
			if (context instanceof Activity) {
				Activity act = (Activity) context;
				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						handler.onNetError();
					}
				});
			} else
				throw new MyException("context is must be Activity.");

		}

		protected void doInBackground() {
			// TODO Auto-generated method stub
			InputStream in = null;

			if (!IOUtils.networkIsAvailable(context)) {
				onNetError();
				return;
			}
			if (IOUtils.networkIsAvailable(context)) {//network is well
				if (type == 0) {// get
					HttpEntity entity = null;
					HttpGet get = new HttpGet(url);
					try {
						HttpResponse res = HttpFactory.execute(context, get);
						final int statusCode = res.getStatusLine()
								.getStatusCode();
						entity = res.getEntity();
						if (statusCode == HttpStatus.SC_OK && entity != null) {
							in = entity.getContent();
							// System.out.println("####"
							// + EntityUtils.toString(entity));
							onPostExecute(in);
						} else {
							get.abort();
						}
					} catch (Exception e) {
						get.abort();
						Log.e(TAG, "____get___" + e.toString() + "_____");
					}
				} else {// post
					final HttpPost post = new HttpPost(url);
					HttpEntity entity = null;
					try {
						// add the url parameters
						List<NameValuePair> pair = new ArrayList<NameValuePair>();
						if (params != null && !params.isEmpty()) {
							for (Map.Entry<String, String> entry : params
									.entrySet()) {
								pair.add(new BasicNameValuePair(entry.getKey(),
										entry.getValue()));
							}
						}
						post.setEntity(new UrlEncodedFormEntity(pair, "UTF-8"));
						final HttpResponse response = HttpFactory.execute(
								context, post);
						final int statusCode = response.getStatusLine()
								.getStatusCode();
						if (statusCode == HttpStatus.SC_OK) {
							entity = response.getEntity();
							if (entity != null) {
								in = entity.getContent();
								onPostExecute(in);
							}
						} else {
							post.abort();
						}
					} catch (Exception e) {
						post.abort();
						Log.e(TAG, "____post___" + e.toString() + "_____");
					}
				}
			}

		}

		/**handler the result*/
		protected void onPostExecute(final InputStream result) {
			if (context instanceof Activity) {
				Activity act = (Activity) context;
				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (result == null) {
							handler.onFail();
						} else {
							handler.onSuccess(handler.parseResult(result));
						}
					}
				});
			} else
				throw new MyException("context is must be Activity.");
			IOUtils.closeStream(result);
		}
	}
}
