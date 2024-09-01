package app.app.post.Service;

import app.app.post.Post;
import app.app.post.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 모든 게시물 가져오기
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // ID로 특정 게시물 가져오기
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    // 게시물 생성
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // 게시물 수정
    public Post updatePost(Long id, Post postDetails) {
        return postRepository.findById(id).map(post -> {
            post.setTitle(postDetails.getTitle());
            post.setContent(postDetails.getContent());
            post.setAuthor(postDetails.getAuthor());
            post.setLocation(postDetails.getLocation());
            post.setTouristSpotId(postDetails.getTouristSpotId());
            // updatedAt은 BaseTimeEntity가 자동으로 처리할 수 있음 (생성자에서 설정된 경우)
            return postRepository.save(post);
        }).orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
    }

    // 게시물 삭제
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
