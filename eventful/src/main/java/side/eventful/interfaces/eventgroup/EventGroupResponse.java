package side.eventful.interfaces.eventgroup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class EventGroupResponse {

    @Getter
    public static class Create {
        private final Long id;
        private final String name;
        private final String description;

        @JsonProperty("image_url")
        private final String imageUrl;

        @JsonProperty("join_password")
        private final String joinPassword;

        private Create(Long id, String name, String description, String imageUrl, String joinPassword) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.imageUrl = imageUrl;
            this.joinPassword = joinPassword;
        }

        public static Create create(Long id, String name, String description, String imageUrl, String joinPassword) {
            return new Create(id, name, description, imageUrl, joinPassword);
        }
    }
}
