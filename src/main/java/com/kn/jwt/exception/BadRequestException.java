package com.kn.jwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BadRequestException extends RuntimeException {

	public BadRequestException(String msg) {
		super(msg);
	}
	
	public BadRequestException() {
        super();
    }
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
    public BadRequestException(Throwable cause) {
        super(cause);
    }
}
