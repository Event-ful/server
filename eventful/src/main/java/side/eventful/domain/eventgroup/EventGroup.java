package side.eventful.domain.eventgroup;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.eventful.domain.common.BaseEntity;
import side.eventful.domain.member.Member;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class EventGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15)
    private String name;

    @Column(length = 200)
    private String description;

    private String imageUrl;

    @Column(nullable = false, length = 8)
    private String joinPassword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private Member leader;

    @OneToMany(mappedBy = "eventGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventGroupMember> members = new ArrayList<>();

    private EventGroup(String name, String description, String imageUrl, String joinPassword, Member leader) {
        validateCreate(name, description, leader);
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.joinPassword = joinPassword;
        this.leader = leader;
        // 그룹장을 첫 번째 멤버로 자동 추가
        this.members.add(EventGroupMember.of(this, leader));
    }

    public static EventGroup create(String name, String description, String imageUrl, Member leader) {
        String generatedPassword = generatePassword();
        return new EventGroup(name, description, imageUrl, generatedPassword, leader);
    }

    public void joinMember(Member member, String inputPassword) {
        validatePassword(inputPassword);
        validateMemberNotExists(member);

        this.members.add(EventGroupMember.of(this, member));
    }

    public void leaveMember(Member member) {
        validateMemberExists(member);
        if (this.leader.equals(member)) {
            throw new IllegalArgumentException("그룹장은 그룹장 위임 후에 탈퇴할 수 있습니다");
        }

        this.members.removeIf(eventGroupMember ->
                eventGroupMember.getMember().equals(member));
    }

    public void removeMember(Member targetMember, Member requestMember) {
        validateLeaderPermission(requestMember);
        validateMemberExists(targetMember);
        validateNotRemovingLeader(targetMember);

        this.members.removeIf(eventGroupMember ->
                eventGroupMember.getMember().equals(targetMember));
    }

    public List<Member> getMembers() {
        return members.stream()
                .map(EventGroupMember::getMember)
                .toList();
    }

    private void validateCreate(String name, String description, Member leader) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("그룹 이름은 필수입니다");
        }
        if (name.length() > 15) {
            throw new IllegalArgumentException("그룹 이름은 15자를 초과할 수 없습니다");
        }
        if (description != null && description.length() > 200) {
            throw new IllegalArgumentException("그룹 소개는 200자를 초과할 수 없습니다");
        }
        if (leader == null) {
            throw new IllegalArgumentException("그룹장은 필수입니다");
        }
    }

    private void validatePassword(String inputPassword) {
        if (!this.joinPassword.equals(inputPassword)) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다");
        }
    }

    private void validateMemberNotExists(Member member) {
        if (this.members.stream().anyMatch(eventGroupMember -> eventGroupMember.getMember().equals(member))) {
            throw new IllegalArgumentException("이미 그룹에 가입된 회원입니다");
        }
    }

    private void validateMemberExists(Member member) {
        if (this.members.stream().noneMatch(eventGroupMember -> eventGroupMember.getMember().equals(member))) {
            throw new IllegalArgumentException("그룹에 가입되지 않은 회원입니다");
        }
    }

    private void validateLeaderPermission(Member requestMember) {
        if (!this.leader.equals(requestMember)) {
            throw new IllegalArgumentException("그룹장만이 회원을 추방할 수 있습니다");
        }
    }

    private void validateNotRemovingLeader(Member targetMember) {
        if (this.leader.equals(targetMember)) {
            throw new IllegalArgumentException("그룹장은 추방할 수 없습니다");
        }
    }

    // 명세서 규칙에 따른 8자 비밀번호 생성: 소문자1개, 대문자1개, 특수문자1개, 나머지5자 랜덤
    private static String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String specialChars = "!@#$%^&*()";
        String allChars = lowercase + uppercase;

        // 필수 문자 1개씩 추가
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // 나머지 5자는 소문자, 대문자 랜덤
        for (int i = 0; i < 5; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 문자열을 랜덤하게 섞기
        return shuffleString(password.toString(), random);
    }

    private static String shuffleString(String input, SecureRandom random) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }

        for (int i = characters.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters.get(i);
            characters.set(i, characters.get(j));
            characters.set(j, temp);
        }

        StringBuilder result = new StringBuilder();
        for (char c : characters) {
            result.append(c);
        }
        return result.toString();
    }
}
