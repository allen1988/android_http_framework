package com.allen.http.framework;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/**
 * the implements of {@link TaskHandler} ,handler the {@link JSONArray}
 */
public abstract class JsonArrayTaskHandler extends TaskHandler<JSONArray> {

	private static final String TAG = JsonArrayTaskHandler.class
			.getSimpleName();

	@Override
	public JSONArray parseResult(InputStream result) {
		try {
			String json = IOUtils.stream2String(result);
			JSONArray jarray = new JSONArray(json);
			return jarray;
		} catch (Exception e) {
			Log.e(TAG, "____" + e.toString() + "_____");
		}
		return null;
	}

}