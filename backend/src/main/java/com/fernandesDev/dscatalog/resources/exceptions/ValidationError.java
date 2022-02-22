package com.fernandesDev.dscatalog.resources.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandarError{

    List<FieldMessage> errors = new ArrayList<>();

    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void addErros(String field, String message){
        errors.add(new FieldMessage(field, message));
    }
}
