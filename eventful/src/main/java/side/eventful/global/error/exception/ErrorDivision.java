package side.eventful.global.error.exception;

public enum ErrorDivision {
    EMAIL_CONFIRM_INVALID_CODE("email-confirm-1"),
    EMAIL_CONFIRM_ALREADY_VERIFIED("email-confirm-2"),
    EMAIL_CONFIRM_EXPIRED("email-confirm-3");

    private final String code;

    ErrorDivision(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
