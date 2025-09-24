package side.eventful.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String errorMessage;
    private String divisionCode;

    public static ErrorResponse of(int statusCode, String errorMessage) {
        return new ErrorResponse(statusCode, errorMessage, null);
    }

    public static ErrorResponse of(int statusCode, String errorMessage, String divisionCode) {
        return new ErrorResponse(statusCode, errorMessage, divisionCode);
    }
}
