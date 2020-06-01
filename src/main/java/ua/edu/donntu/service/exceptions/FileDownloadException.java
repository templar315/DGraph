package ua.edu.donntu.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error while downloading file")
public class FileDownloadException extends Exception {
    public FileDownloadException(String errorMessage) {
        super(errorMessage);
    }
}
