package pl.hycom.mokka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.FileNotFoundException;

/**
 * Controller responsible to handling exceptions throws across the whole application
 *
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@ControllerAdvice
public class ExceptionHandlingController {
    /**
     * Method responsible for handling FileNotFoundException and returns HttpStatus.NOT_FOUND
     */
    @ResponseStatus(value = HttpStatus.NOT_FOUND,
                    reason = "Couldn't find file with given name")
    @ExceptionHandler(FileNotFoundException.class)
    public void fileNotFoundException() {
        // Nothing to do
    }

    /**
     * Method responsible for handling IllegalArgumentException and returns HttpStatus.BAD_REQUEST
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public void illegalArgumentException() {
        // Nothing to do
    }
}
