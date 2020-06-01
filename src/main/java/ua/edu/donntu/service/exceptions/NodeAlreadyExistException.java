package ua.edu.donntu.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Node already exist")
public class NodeAlreadyExistException extends Exception {
    public NodeAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}
