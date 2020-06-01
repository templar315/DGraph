package ua.edu.donntu.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error while saving file")
public class FileSaveException extends Exception {
    public FileSaveException(String errorMessage) {
        super(errorMessage);
    }
}
