package side.eventful.domain.eventgroup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventGroupTest {

    @Test
    @DisplayName("이벤트 생성 - 정상 케이스")
    void create_WithValidInput_ReturnEventGroup() {
        // given
        String name = "Event Group Name";
        String description = "This is a description for the event group.";
        String imageUrl = "http://example.com/image.png";

        // when
        EventGroup eventGroup = EventGroup.create(name, description, imageUrl);

        // then
        assertNotNull(eventGroup);
        assertEquals(name, eventGroup.getName());
        assertEquals(description, eventGroup.getDescription());
        assertEquals(imageUrl, eventGroup.getImageUrl());
    }

    @Test
    @DisplayName("이벤트 생성 - 그룹명 누락 케이스")
    void create_WithNoInputName_ThrowsException() {
        // given
        String name = null;
        String description = "This is a description for the event group.";
        String imageUrl = "http://example.com/image.png";

        // when & then
        assertThrows(IllegalArgumentException.class, () ->  EventGroup.create(name, description, imageUrl));
    }

    @Test
    @DisplayName("이벤트 생성 - 그룹 설명 누락 케이스")
    void create_WithNoInputDescription_ThrowsException() {
        // given
        String name = "Event Group Name";
        String description = null;
        String imageUrl = "http://example.com/image.png";

        // when & then
        assertThrows(IllegalArgumentException.class, () ->  EventGroup.create(name, description, imageUrl));
    }

}
