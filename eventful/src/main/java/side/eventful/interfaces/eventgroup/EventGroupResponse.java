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

    @Getter
    public static class Get {
        @JsonProperty("group_name")
        private final String groupName;

        @JsonProperty("group_description")
        private final String groupDescription;

        @JsonProperty("is_leader")
        private final boolean isLeader;

        @JsonProperty("member_count")
        private final int memberCount;

        @JsonProperty("join_code")
        private final String joinCode;

        @JsonProperty("group_password")
        private final String groupPassword;

        @JsonProperty("group_member")
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
        @JsonProperty("member_id")
        private final Long memberId;

        @JsonProperty("member_name")
        private final String memberName;

        @JsonProperty("is_leader")
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
        @JsonProperty("group_id")
        private final Long groupId;

        private Join(Long groupId) {
            this.groupId = groupId;
        }

        public static Join create(Long groupId) {
            return new Join(groupId);
        }
    }
}
