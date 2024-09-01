package app.app.post;

import app.app.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //게시물 ID

    @Column(length = 500, nullable = false)
    private String title; //게시물 제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; //내용

    private String author; //작성자

    private String location; //위치정보

    private String createdAt; // 작성일

    private String updatedAt; // 수정일

    private String touristSpotId; //관광지 ID ==> 게시물이 어느 관광지에 대한 것인지 확인

    @Builder
    public Post(Long id, String title, String content, String author, String location, String createdAt, String updatedAt, String touristSpotId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.location=location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.touristSpotId=touristSpotId;
    }
}