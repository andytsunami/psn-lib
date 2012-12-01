package com.krobothsoftware.commons.parse;

public class ParseException extends Exception {
	private static final long serialVersionUID = 8013353025740184267L;

	public ParseException(final String error) {
		super(error);
	}

	public ParseException(final Throwable throwable) {
		super(throwable);
	}

	public ParseException(final String message, final Throwable throwable) {
		super(message, throwable);
	}

}
