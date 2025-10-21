package side.eventful.domain.eventgroup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import side.eventful.domain.member.Member;

public class EventGroupCommand {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Create {
        private String name;
        private String description;
        private String imageUrl;
        private Member leader;

        public static Create create(String name, String description, String imageUrl, Member leader) {
            return new Create(name, description, imageUrl, leader);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Join {
        private Long eventGroupId;
        private Member member;
        private String joinPassword;

        public static Join create(Long eventGroupId, Member member, String joinPassword) {
            return new Join(eventGroupId, member, joinPassword);
        }
    }
}
