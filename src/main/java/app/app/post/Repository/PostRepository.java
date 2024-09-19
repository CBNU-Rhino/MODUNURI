package app.app.post.Repository;

import app.app.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 제목에 특정 문자열이 포함된 게시물을 검색
    List<Post> findByTitleContainingIgnoreCase(String title);
}
