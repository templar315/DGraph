package ua.edu.donntu.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Node does not exist")
public class NodeDoesNotExistException extends Exception {
    public NodeDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
