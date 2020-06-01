package ua.edu.donntu.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Non nullable field is empty")
public class EmptyNullableFieldException extends Exception {
    public EmptyNullableFieldException(String errorMessage) {
        super(errorMessage);
    }
}
