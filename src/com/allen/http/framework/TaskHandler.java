package com.allen.http.framework;

import java.io.InputStream;

/**
 * when you call {@link Request#request(String, TaskHandler)} ,you should
 * implement this, override the {@link #onNetError()},
 * {@link #onSuccess(T result)} ,{@link #onFail()},
 * {@link #parseResult(InputStream result)}
 */
public abstract class TaskHandler<T> {
	/** network is break */
	public abstract void onNetError();

	/**
	 * have a successful response
	 * 
	 * @param result
	 */
	public abstract void onSuccess(T result);

	/** if the timeout,server error */
	public abstract void onFail();

	/**
	 * parse the InputStream,must be override this
	 * 
	 * @param result
	 */
	public abstract T parseResult(InputStream result);
}
