package com.allen.http.framework;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import android.util.Log;

/**
 * the implements of {@link TaskHandler} ,handler the {@link String}
 */
public abstract class StringTaskHandler extends TaskHandler<String> {

	private static final String TAG = StringTaskHandler.class.getSimpleName();

	@Override
	public String parseResult(InputStream result) {
		try {
			return IOUtils.stream2String(result);
		} catch (IOException e) {
			Log.e(TAG, "____" + e.toString() + "_____");
		}
		return null;
	}

}
