package app.app.course;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ContentInfo {
    private String contentId;     // 관광지 ID
    private String contentTypeId; // 관광지 타입 ID

    // 기본 생성자
    public ContentInfo() {}

    // 생성자
    public ContentInfo(String contentId, String contentTypeId) {
        this.contentId = contentId;
        this.contentTypeId = contentTypeId;
    }
}
