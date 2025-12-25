package side.eventful.domain.event;

public enum ParticipantRole {
    CREATOR("이벤트 생성자"),
    PARTICIPANT("일반 참여자");

    private final String description;

    ParticipantRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
