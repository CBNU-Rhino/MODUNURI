package app.app.post.Controller;

import app.app.post.Post;
import app.app.post.Service.PostService;
import app.app.user.CustomUserDetails;
import app.app.user.Service.UserService;
import app.app.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    // 모든 게시물 가져오기
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam(value = "search", required = false) String searchTerm) {
        System.out.println("검색어: '" + searchTerm + "'"); // 검색어가 null인지 빈 문자열인지 확인
        List<Post> posts;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            posts = postService.searchPostsByTitle(searchTerm);
        } else {
            posts = postService.getAllPosts();
        }
        return ResponseEntity.ok(posts);
    }



    // ID로 특정 게시물 가져오기
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // 게시물 생성하기
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        // 현재 로그인한 사용자의 ID와 이름을 가져와서 author와 userId 필드에 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            post.setAuthor(userDetails.getRealUsername()); // 실제 username 설정
            post.setUserId(userDetails.getUsername()); // ID 설정
        }

        // 게시물 생성일 자동 설정
        post.setCreatedAt(LocalDateTime.now());

        Post createdPost = postService.createPost(post);
        return ResponseEntity.ok(createdPost);
    }

    // 게시물 수정하기
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post postDetails, Authentication authentication) {
        // 인증된 사용자로부터 CustomUserDetails 가져오기
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // 기존 게시물 정보 불러오기
            Post existingPost = postService.getPostById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));

            // postDetails에서 제목과 내용을 기존 게시물에 적용
            existingPost.setTitle(postDetails.getTitle());
            existingPost.setContent(postDetails.getContent());
            existingPost.setUpdatedAt(LocalDateTime.now());

            // author를 자동으로 설정 (로그인된 사용자 이름으로 설정)
            existingPost.setAuthor(userDetails.getRealUsername());

            // 게시물 업데이트 및 저장
            Post updatedPost = postService.save(existingPost);

            return ResponseEntity.ok(updatedPost);
        } else {
            // 인증 정보가 없으면 오류 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 게시물 삭제하기
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // 게시물 상세 정보 가져오기
    @GetMapping("/post/{id}")
    public String getPostDetail(@PathVariable Long id, Model model, Principal principal) {
        // 로그인한 사용자의 정보를 가져옴
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            model.addAttribute("currentUser", user); // currentUser 정보를 모델에 추가
        }

        Post post = postService.findById(id);
        model.addAttribute("post", post);
        return "post-detail"; // Thymeleaf 템플릿
    }
}