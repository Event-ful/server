package side.eventful.application.eventgroup;

import lombok.Getter;

public class EventGroupResult {

    @Getter
    public static class Create {
        private final Long id;
        private final String name;
        private final String description;
        private final String imageUrl;
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
