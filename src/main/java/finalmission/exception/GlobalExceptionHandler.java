package finalmission.exception;

import finalmission.exception.custom.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> exceptionHandle(
            final CustomException e
    ){
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getStatus().value());
        return ResponseEntity
                .status(e.getStatus())
                .body(errorResponse);
    }
}
