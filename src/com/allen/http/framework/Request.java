package com.allen.http.framework;

import java.util.Map;

/**
 * A interface of request http server by get and post
 */
public interface Request {
	
	void request(String url, TaskHandler handler);

	void request(String url, Map<String, String> params, TaskHandler handler);
}
