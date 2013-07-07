package com.allen.http.framework;

/** custom exception */
public class MyException extends Exception {
	String message;

	public MyException(String e) {
		message = e;
	}

	public String getMessage() {
		return message;
	}
}
