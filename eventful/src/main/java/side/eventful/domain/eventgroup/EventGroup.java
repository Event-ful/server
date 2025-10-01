package side.eventful.domain.eventgroup;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.eventful.domain.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
public class EventGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String imageUrl;

    private EventGroup(String name, String description, String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static EventGroup create(String name, String description, String imageUrl) {
        return new EventGroup(name, description, imageUrl);
    }
}
