package app.app.post.Service;

import app.app.post.Post;
import app.app.post.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PreAuthorize("isAuthenticated()")
    public List<Post> getAllPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    // ID로 특정 게시물 가져오기
    @PreAuthorize("isAuthenticated()")
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    // 게시물 생성
    @PreAuthorize("isAuthenticated()")
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // 게시물 수정
    @PreAuthorize("isAuthenticated()")
    public Post updatePost(Long id, Post postDetails) {
        return postRepository.findById(id).map(post -> {
            post.setTitle(postDetails.getTitle());
            post.setUserId(postDetails.getUserId());
            post.setContent(postDetails.getContent());
            post.setAuthor(postDetails.getAuthor());
            post.setLocation(postDetails.getLocation());
            post.setTouristSpotId(postDetails.getTouristSpotId());
            return postRepository.save(post);
        }).orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
    }

    // 게시물 삭제
    @PreAuthorize("isAuthenticated()")
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
    @PreAuthorize("isAuthenticated()")
    // 게시물 저장 (새로운 게시물 또는 수정된 게시물 저장)
    public Post save(Post post) {
        return postRepository.save(post);
    }
    @PreAuthorize("isAuthenticated()")
    // 커스텀 findById 로직
    public Post findById(Long id) {
        // 기본 findById에서 추가적으로 처리하고 싶은 로직을 추가 가능
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No post found with id: " + id));
    }

    // 제목에 검색어가 포함된 게시물 반환
    @PreAuthorize("isAuthenticated()")
    public List<Post> searchPostsByTitle(String searchTerm) {
        return postRepository.findByTitleContainingIgnoreCase(searchTerm);
    }
}
