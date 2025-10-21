package side.eventful.interfaces.eventgroup;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EventGroupRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {
        @NotBlank(message = "그룹 이름은 필수입니다")
        @Size(max = 15, message = "그룹 이름은 15자를 초과할 수 없습니다")
        private String name;

        @Size(max = 200, message = "그룹 소개는 200자를 초과할 수 없습니다")
        private String description;

        @JsonProperty("image_url")
        private String imageUrl;

        public static Create create(String name, String description, String imageUrl) {
            Create request = new Create();
            request.name = name;
            request.description = description;
            request.imageUrl = imageUrl;
            return request;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Join {
        @NotBlank(message = "참가 비밀번호는 필수입니다")
        @Size(min = 8, max = 8, message = "참가 비밀번호는 8자여야 합니다")
        @JsonProperty("join_password")
        private String joinPassword;

        public static Join create(String joinPassword) {
            Join request = new Join();
            request.joinPassword = joinPassword;
            return request;
        }
    }
}
