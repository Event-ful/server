package side.eventful.domain.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 파일 저장 디렉토리 Enum
 * 허용된 디렉토리만 사용하도록 제한
 */
@Getter
@RequiredArgsConstructor
public enum FileDirectory {

    GENERAL("general", "일반 파일"),
    GROUP_IMAGES("group-images", "그룹 이미지"),
    PROFILE_IMAGES("profile-images", "프로필 이미지");

    private final String path;
    private final String description;
}
