package ua.edu.donntu.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error while deleting file")
public class FileDeleteException extends Exception {
    public FileDeleteException(String errorMessage) {
        super(errorMessage);
    }
}
