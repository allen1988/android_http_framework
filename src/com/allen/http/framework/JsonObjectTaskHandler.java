package com.allen.http.framework;

import java.io.InputStream;

import org.json.JSONObject;

import android.util.Log;

/**
 * the implements of {@link TaskHandler} ,handler the {@link JSONObject}
 */
public abstract class JsonObjectTaskHandler extends TaskHandler<JSONObject> {

	private static final String TAG = JsonObjectTaskHandler.class.getSimpleName();

	@Override
	public JSONObject parseResult(InputStream result) {
		try {
			String json = IOUtils.stream2String(result);
			JSONObject jobj = new JSONObject(json);
			return jobj;
		} catch (Exception e) {
			Log.e(TAG, "____" + e.toString() + "_____");
		}
		return null;
	}

}