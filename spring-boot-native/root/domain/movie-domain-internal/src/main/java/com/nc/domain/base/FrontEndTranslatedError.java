package com.nc.domain.base;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class FrontEndTranslatedError {

	@NotBlank
	@Size(max = 1024)
	public String message;

	public FrontEndTranslatedError(String message) {
		this.message = message;
	}

	public FrontEndTranslatedError() {
		super();
	}
}
