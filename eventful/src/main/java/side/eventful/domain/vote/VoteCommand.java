package side.eventful.domain.vote;

import java.time.LocalTime;
import java.util.List;

/**
 * 투표(Vote) 관련 Command 객체
 */
public class VoteCommand {

    /**
     * 투표 생성 커맨드
     */
    public record CreateVote(
        String name,
        String memo,
        LocalTime startTime,
        LocalTime endTime,
        List<String> locationOptions
    ) {
    }

    /**
     * 투표 옵션 추가 커맨드
     */
    public record AddOption(
        String locationName
    ) {
    }

    /**
     * 투표하기 커맨드
     */
    public record CastVote(
        Long optionId
    ) {
    }
}

