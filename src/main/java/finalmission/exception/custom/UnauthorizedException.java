package finalmission.exception.custom;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CustomException{

    public UnauthorizedException(final String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
