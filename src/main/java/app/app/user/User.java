package app.app.user;

import app.app.course.Course;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @Column(name = "user_id")
    private String userId;

    // 관심 관광지 저장을 위한 필드
    // 관심 관광지를 contentId와 contentTypeId로 저장
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_favorite_contents", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "content_id")
    @Column(name = "content_type_id")
    private Map<String, String> favoriteContents = new HashMap<>();
    // 새로운 필드: 유저가 여러 코스를 가질 수 있음
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses = new ArrayList<>();

    // 기본 생성자
    public User() {}

    // 생성자
    public User(String username, String password, String userId) {
        this.username = username;
        this.password = password;
        this.userId = userId;
    }

    // 관심 관광지 추가 메서드
    public void addFavoriteContent(String contentId, String contentTypeId) {
        this.favoriteContents.put(contentId, contentTypeId);
    }


    // 관심 관광지 제거 메서드
    public void removeFavoriteContent(String contentId) {
        this.favoriteContents.remove(contentId);
    }

    // 코스 추가 메서드
    public void addCourse(Course course) {
        this.courses.add(course);
        course.setUser(this);  // 양방향 관계를 유지하기 위해 설정
    }

    // 코스 삭제 메서드
    public void removeCourse(Course course) {
        this.courses.remove(course);
        course.setUser(null);  // 양방향 관계 정리
    }
}