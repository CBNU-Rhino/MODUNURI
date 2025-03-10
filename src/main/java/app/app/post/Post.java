package app.app.post;

import app.app.BaseTimeEntity;
import app.app.comment.Comment;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String author; //작성자_닉네임

    private String userId; //userId

    private String location; //위치정보

    private LocalDateTime createdAt; // 작성일

    private LocalDateTime updatedAt; // 수정일

    private String touristSpotId; //관광지 ID ==> 게시물이 어느 관광지에 대한 것인지 확인

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(Long id, String title, String content, String author, String location, LocalDateTime createdAt, LocalDateTime updatedAt, String touristSpotId) {
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