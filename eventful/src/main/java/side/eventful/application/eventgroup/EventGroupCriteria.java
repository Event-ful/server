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
        private String groupPassword;

        public static Join create(Long eventGroupId, String groupPassword) {
            return new Join(eventGroupId, groupPassword);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Update {
        private Long eventGroupId;
        private String name;
        private String description;
        private String imageUrl;

        public static Update create(Long eventGroupId, String name, String description, String imageUrl) {
            return new Update(eventGroupId, name, description, imageUrl);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Get {
        private Long eventGroupId;

        public static Get create(Long eventGroupId) {
            return new Get(eventGroupId);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class VerifyCode {
        private String joinCode;

        public static VerifyCode create(String joinCode) {
            return new VerifyCode(joinCode);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class RemoveMember {
        private Long eventGroupId;
        private Long targetMemberId;

        public static RemoveMember create(Long eventGroupId, Long targetMemberId) {
            return new RemoveMember(eventGroupId, targetMemberId);
        }
    }

    @Getter
    public static class GetList {
        // 별도의 파라미터 없음 - 세션에서 회원 정보를 가져올 예정

        private GetList() {
        }

        public static GetList create() {
            return new GetList();
        }
    }

    // 그룹장 위임 Criteria
    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class TransferLeader {
        private Long eventGroupId;
        private Long newLeaderMemberId;

        public static TransferLeader create(Long eventGroupId, Long newLeaderMemberId) {
            return new TransferLeader(eventGroupId, newLeaderMemberId);
        }
    }
}
