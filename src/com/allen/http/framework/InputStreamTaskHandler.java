package com.allen.http.framework;

import java.io.InputStream;

/**
 * the implements of {@link TaskHandler} ,handler the {@link InputStream}
 */
public abstract class InputStreamTaskHandler extends TaskHandler<InputStream> {

	@Override
	public InputStream parseResult(InputStream result) {
		// TODO Auto-generated method stub
		return result;
	}

}
