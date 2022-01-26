package com.fernandesDev.dscatalog.resources.exceptions;

import com.fernandesDev.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice //Escuta as exceptions lançadas nos Controllers
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class) //Capturando exception específica
    public ResponseEntity<StandarError> notFoundExceptionResponseEntity(ResourceNotFoundException e, HttpServletRequest request){
        StandarError error = new StandarError();
        error.setTimestamp(Instant.now());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError("Resource not found");
        error.setMessage(e.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
