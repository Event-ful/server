package side.eventful.global.error.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int statusCode;
    private final String divisionCode;

    public BusinessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.divisionCode = null;
    }

    public BusinessException(int statusCode, String message, ErrorDivision divisionCode) {
        super(message);
        this.statusCode = statusCode;
        this.divisionCode = divisionCode.getCode();
    }
}
