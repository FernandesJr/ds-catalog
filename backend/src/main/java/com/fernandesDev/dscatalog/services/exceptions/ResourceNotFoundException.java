package com.fernandesDev.dscatalog.services.exceptions;


public class ResourceNotFoundException extends RuntimeException{
	private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
