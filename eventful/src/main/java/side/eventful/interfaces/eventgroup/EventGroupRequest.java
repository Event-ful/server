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
        @JsonProperty("group_password")
        private String groupPassword;

        public static Join create(String groupPassword) {
            Join request = new Join();
            request.groupPassword = groupPassword;
            return request;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        @JsonProperty("group_name")
        @Size(max = 15, message = "그룹 이름은 15자를 초과할 수 없습니다")
        private String groupName;

        @JsonProperty("group_description")
        @Size(max = 200, message = "그룹 소개는 200자를 초과할 수 없습니다")
        private String groupDescription;

        @JsonProperty("group_image")
        private String groupImage;

        public static Update create(String groupName, String groupDescription, String groupImage) {
            Update request = new Update();
            request.groupName = groupName;
            request.groupDescription = groupDescription;
            request.groupImage = groupImage;
            return request;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class VerifyCode {
        @NotBlank(message = "참가 코드는 필수입니다")
        @JsonProperty("join_code")
        private String joinCode;

        public static VerifyCode create(String joinCode) {
            VerifyCode request = new VerifyCode();
            request.joinCode = joinCode;
            return request;
        }
    }
}
