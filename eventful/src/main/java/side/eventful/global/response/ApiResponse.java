package side.eventful.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Schema(description = "API 응답 객체")
public class ApiResponse<T> {
    @Schema(description = "HTTP 상태 코드", example = "200")
    private int statusCode;
    @Schema(description = "응답 데이터")
    private T data;

    public static <T> ApiResponse<T> of(int statusCode, T data) {
        return new ApiResponse<>(statusCode, data);
    }

    public static <T> ApiResponse<String> ok() {
        return new ApiResponse<>(HttpStatus.OK.value(), "Success");
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), data);
    }
}
