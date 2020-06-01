package ua.edu.donntu.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Object is not unique")
public class ObjectUniquenessException extends Exception {
    public ObjectUniquenessException(String errorMessage) {
        super(errorMessage);
    }
}
