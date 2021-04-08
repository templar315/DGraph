package ua.edu.donntu.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Measurement not finished")
public class MeasurementNotFinishedException extends Exception {
    public MeasurementNotFinishedException(String errorMessage) {
        super(errorMessage);
    }
}
