package side.eventful.interfaces.eventgroup;

import lombok.Getter;

public class EventGroupResponse {

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

    @Getter
    public static class Get {
        private final String groupName;
        private final String groupDescription;
        private final boolean isLeader;
        private final int memberCount;
        private final String joinCode;
        private final String groupPassword;
        private final java.util.List<GroupMember> groupMembers;

        private Get(String groupName, String groupDescription, boolean isLeader, int memberCount,
                   String joinCode, String groupPassword, java.util.List<GroupMember> groupMembers) {
            this.groupName = groupName;
            this.groupDescription = groupDescription;
            this.isLeader = isLeader;
            this.memberCount = memberCount;
            this.joinCode = joinCode;
            this.groupPassword = groupPassword;
            this.groupMembers = groupMembers;
        }

        public static Get create(String groupName, String groupDescription, boolean isLeader, int memberCount,
                               String joinCode, String groupPassword, java.util.List<GroupMember> groupMembers) {
            return new Get(groupName, groupDescription, isLeader, memberCount, joinCode, groupPassword, groupMembers);
        }
    }

    @Getter
    public static class GroupMember {
        private final Long memberId;
        private final String memberName;
        private final boolean isLeader;

        private GroupMember(Long memberId, String memberName, boolean isLeader) {
            this.memberId = memberId;
            this.memberName = memberName;
            this.isLeader = isLeader;
        }

        public static GroupMember create(Long memberId, String memberName, boolean isLeader) {
            return new GroupMember(memberId, memberName, isLeader);
        }
    }

    @Getter
    public static class Join {
        private final Long groupId;

        private Join(Long groupId) {
            this.groupId = groupId;
        }

        public static Join create(Long groupId) {
            return new Join(groupId);
        }
    }

    @Getter
    public static class VerifyCode {
        private final Long groupId;
        private final String groupName;
        private final String groupDescription;

        private VerifyCode(Long groupId, String groupName, String groupDescription) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.groupDescription = groupDescription;
        }

        public static VerifyCode create(Long groupId, String groupName, String groupDescription) {
            return new VerifyCode(groupId, groupName, groupDescription);
        }
    }

    @Getter
    public static class GetList {
        private final java.util.List<GroupSummary> groups;

        private GetList(java.util.List<GroupSummary> groups) {
            this.groups = groups;
        }

        public static GetList create(java.util.List<GroupSummary> groups) {
            return new GetList(groups);
        }
    }

    @Getter
    public static class GroupSummary {
        private final Long groupId;
        private final String groupName;
        private final String groupDescription;
        private final String groupImageUrl;
        private final int memberCount;

        private GroupSummary(Long groupId, String groupName, String groupDescription, String groupImageUrl, int memberCount) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.groupDescription = groupDescription;
            this.groupImageUrl = groupImageUrl;
            this.memberCount = memberCount;
        }

        public static GroupSummary create(Long groupId, String groupName, String groupDescription, String groupImageUrl, int memberCount) {
            return new GroupSummary(groupId, groupName, groupDescription, groupImageUrl, memberCount);
        }
    }
}
