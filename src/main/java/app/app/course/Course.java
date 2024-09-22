package app.app.course;

import app.app.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseName;  // 코스 이름

    @ElementCollection
    @CollectionTable(name = "course_content", joinColumns = @JoinColumn(name = "course_id"))
    private List<ContentInfo> contentInfos = new ArrayList<>();  // contentId와 contentTypeId를 함께 저장하는 리스트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // 각 코스는 하나의 유저와 연관됨

    // 기본 생성자
    public Course() {}

    // 수정된 생성자: contentInfos 포함
    public Course(String courseName, List<ContentInfo> contentInfos, User user) {
        this.courseName = courseName;
        this.contentInfos = contentInfos;  // contentInfos 초기화
        this.user = user;
    }

    // 관광지 contentInfo 추가 메서드
    public void addContentInfo(ContentInfo contentInfo) {
        this.contentInfos.add(contentInfo);
    }

    // 관광지 contentInfo 삭제 메서드
    public void removeContentInfo(ContentInfo contentInfo) {
        this.contentInfos.remove(contentInfo);
    }
}
