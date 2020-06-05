package ua.edu.donntu.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Incorrect message digest")
public class MessageDigestException extends Exception {
    public MessageDigestException(String errorMessage) {
        super(errorMessage);
    }
}
