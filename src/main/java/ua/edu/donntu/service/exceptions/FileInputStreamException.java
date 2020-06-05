package ua.edu.donntu.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "File input stream error")
public class FileInputStreamException extends Exception {
    public FileInputStreamException(String errorMessage) {
        super(errorMessage);
    }
}
