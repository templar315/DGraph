package ua.edu.donntu.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Message already exist")
public class MessageAlreadyExistException extends Exception {
    public MessageAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}
