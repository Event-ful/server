package side.eventful.global.error.exception;

public enum ErrorDivision {
    ;

    private final String code;

    ErrorDivision(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
