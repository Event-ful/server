package side.eventful.application.eventgroup;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class EventGroupCriteria {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Create {
        private String name;
        private String description;
        private String imageUrl;

        public static Create create(String name, String description, String imageUrl) {
            return new Create(name, description, imageUrl);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Join {
        private Long eventGroupId;
        private String joinPassword;

        public static Join create(Long eventGroupId, String joinPassword) {
            return new Join(eventGroupId, joinPassword);
        }
    }
}
