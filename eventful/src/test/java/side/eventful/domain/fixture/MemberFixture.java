package side.eventful.domain.fixture;

import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.domain.member.Member;

import java.lang.reflect.Field;

/**
 * 테스트용 Member 생성 헬퍼 클래스
 * 도메인의 순수성을 유지하면서 테스트에서 ID를 설정할 수 있도록 지원
 */
public class MemberFixture {

    private static Long memberIdSequence = 1L;

    /**
     * ID가 자동으로 설정된 Member 생성
     */
    public static Member createWithId(String email, String password, String nickname, PasswordEncoder encoder) {
        Member member = Member.create(email, password, nickname, encoder);
        setId(member, memberIdSequence++);
        return member;
    }

    /**
     * 특정 ID를 가진 Member 생성
     */
    public static Member createWithId(Long id, String email, String password, String nickname, PasswordEncoder encoder) {
        Member member = Member.create(email, password, nickname, encoder);
        setId(member, id);
        return member;
    }

    /**
     * 테스트 시퀀스 초기화 (각 테스트 클래스의 @BeforeEach에서 호출)
     */
    public static void resetSequence() {
        memberIdSequence = 1L;
    }

    private static void setId(Object entity, Long id) {
        try {
            Field idField = findIdField(entity.getClass());
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set id for test fixture", e);
        }
    }

    private static Field findIdField(Class<?> clazz) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findIdField(clazz.getSuperclass());
            }
            throw e;
        }
    }
}

