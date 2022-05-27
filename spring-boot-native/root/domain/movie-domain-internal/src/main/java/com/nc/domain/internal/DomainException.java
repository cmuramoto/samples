package com.nc.domain.internal;

import com.nc.domain.base.FrontEndTranslatedError;

public class DomainException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	final FrontEndTranslatedError translation;

	public DomainException(FrontEndTranslatedError translation) {
		this("", translation);
	}

	public DomainException(String message, FrontEndTranslatedError translation) {
		this(message, null, translation);
	}

	public DomainException(String message, Throwable cause, FrontEndTranslatedError translation) {
		super(message, cause, true, false);
		this.translation = translation;
	}

	public DomainException(Throwable cause, FrontEndTranslatedError translation) {
		this("", cause, translation);
	}

	public FrontEndTranslatedError getTranslation() {
		return translation;
	}
}
