package com.fernandesDev.dscatalog.services.exceptions;

import java.time.Instant;

public class EntityNotFoundException extends RuntimeException{

    public EntityNotFoundException(String message) {
        super(message);
    }
}
