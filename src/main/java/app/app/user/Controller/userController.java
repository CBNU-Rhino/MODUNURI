package app.app.user.Controller;

import app.app.user.CustomUserDetails;
import app.app.user.DTO.UserDTO;
import app.app.user.Repository.UserRepository;
import app.app.user.User;
import app.app.user.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class userController {

    @Autowired
    private UserService userService;

    @Autowired  // UserRepository 주입
    private UserRepository userRepository;

    // 회원가입 페이지를 반환하는 GET 메서드
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("userDTO", new UserDTO()); // 폼에서 사용할 빈 객체를 모델에 추가
        return "users/signup"; // templates/users/signup.html 파일을 반환
    }

    // 회원가입 요청을 처리하는 POST 메서드
    @PostMapping("/signup")
    public String register(@ModelAttribute UserDTO userDTO, RedirectAttributes redirectAttributes) {
        try {
            // UserDTO를 사용하여 사용자 등록
            User newUser = userService.registerUser(
                    userDTO.getUsername(),
                    userDTO.getPassword(),
                    userDTO.getUserId());
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다!");
            return "redirect:/users/signup_complete"; // 회원가입 완료 페이지로 리다이렉트
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/signup"; // 오류 시 다시 회원가입 페이지로 리다이렉트
        }
    }

    @GetMapping("/signup_complete")
    public String signupComplete() {
        return "users/signup_complete"; // resources/templates/signup_complete.html을 렌더링
    }

    // 로그인 폼을 표시하는 GET 메서드
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new UserDTO()); // UserDTO 객체를 생성하여 모델에 추가
        return "users/login"; // 로그인 템플릿 반환
    }

    // My Page를 표시하는 GET 메서드
    @GetMapping("/mypage")
    public String showMyPage(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        if (customUserDetails != null) {
            // 사용자 이름 가져오기
            String realUsername = customUserDetails.getRealUsername();

            // 사용자의 관심 관광지 목록 가져오기
            Map<String, String> favoriteContents = customUserDetails.getUser().getFavoriteContents();

            // 로그로 사용자 확인
            System.out.println("사용자 이름: " + realUsername);
            System.out.println("관심 관광지 목록: " + favoriteContents);

            // 모델에 사용자 이름과 관심 관광지 목록 추가
            model.addAttribute("username", realUsername);
            model.addAttribute("favoriteContents", favoriteContents);
        } else {
            // 인증되지 않은 경우에 대한 처리
            model.addAttribute("errorMessage", "로그인이 필요합니다.");
        }

        return "users/mypage"; // My Page 템플릿 반환
    }


    // 관심 관광지 추가를 처리하는 POST 메서드
    @PostMapping("/addFavorite")
    public String addFavorite(
            @RequestParam("contentId") String contentId,
            @RequestParam("contentTypeId") String contentTypeId,
            RedirectAttributes redirectAttributes) {

        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            // contentId와 contentTypeId를 함께 저장하는 메서드 호출
            userService.addFavoriteContent(email, contentId, contentTypeId);
            redirectAttributes.addFlashAttribute("message", "관심 관광지가 추가되었습니다!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/users/mypage"; // My Page로 리다이렉트
    }

    @PostMapping("/removeFavorite")
    public String removeFavorite(@RequestParam("contentId") String contentId, RedirectAttributes redirectAttributes) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // sout으로 로그 확인
        System.out.println("삭제할 contentId: " + contentId);
        System.out.println("사용자 이메일: " + email);

        try {
            userService.removeFavoriteContent(email, contentId);

            Map<String, String> favoriteContents = userService.getFavoriteContents(email);
            System.out.println("After deletion, fetched favorite contents: " + favoriteContents);

            System.out.println("관심 관광지 삭제 성공: " + contentId); // 삭제 성공 시
            redirectAttributes.addFlashAttribute("message", "관심 관광지가 삭제되었습니다!");
        } catch (RuntimeException e) {
            System.out.println("삭제 중 오류 발생: " + e.getMessage()); // 예외 발생 시
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users/mypage"; // My Page로 리다이렉트
    }


    @PostMapping("/save")
    public ResponseEntity<String> saveFavoriteContent(
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        // 로그인 여부 확인
        if (customUserDetails == null || customUserDetails.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");  // 401 반환
        }

        User user = customUserDetails.getUser();  // CustomUserDetails에서 User 객체 추출
        String contentId = requestBody.get("contentId");
        String contentTypeId = requestBody.get("contentTypeId");

        // 데이터 유효성 검사
        if (contentId == null || contentId.isEmpty()) {
            return ResponseEntity.badRequest().body("contentId가 없습니다.");
        }
        if (contentTypeId == null || contentTypeId.isEmpty()) {
            return ResponseEntity.badRequest().body("contentTypeId가 없습니다.");
        }

        // 관심 콘텐츠 저장 로직
        userService.saveFavoriteContent(user, contentId, contentTypeId);
        return ResponseEntity.ok("저장이 완료되었습니다.");
    }




    @PostMapping("/checkSaved")
    public ResponseEntity<Boolean> checkSavedContent(
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        String contentId = requestBody.get("contentId");
        String contentTypeId = requestBody.get("contentTypeId");  // contentTypeId도 받아옴

        // 인증된 사용자 정보가 null이 아닌지 확인
        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        User user = customUserDetails.getUser();  // CustomUserDetails에서 User 객체 추출

        // 사용자의 즐겨찾기 목록(Map의 keySet)에서 contentId와 contentTypeId 확인
        boolean isSaved = user.getFavoriteContents().entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(contentId) && entry.getValue().equals(contentTypeId));  // contentId와 contentTypeId 모두 일치하는지 확인

        return ResponseEntity.ok(isSaved);
    }


    @GetMapping("/getFavoriteContents")
    public ResponseEntity<Map<String, String>> getFavoriteContents(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails != null) {
            // 항상 최신 DB 데이터 가져오기
            User user = userRepository.findByUserId(customUserDetails.getUser().getUserId());
            Map<String, String> favoriteContents = user.getFavoriteContents();
            System.out.println("Fetched favorite contents: " + favoriteContents);
            return ResponseEntity.ok(favoriteContents);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


}
