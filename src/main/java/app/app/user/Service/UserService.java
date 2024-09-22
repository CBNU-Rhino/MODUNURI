package app.app.user.Service;

import app.app.user.User;
import app.app.user.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 사용자 등록 메서드
    public User registerUser(String username, String password, String ID) {
        // 이메일 중복 체크
        if (userRepository.findByUserId(ID) != null) {
            throw new RuntimeException("이미 사용 중인 ID입니다.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("비밀번호는 필수 입력 사항입니다.");
        }
        // 비밀번호 중복 체크
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("이미 사용 중인 비밀번호입니다.");
            }
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User(username, encodedPassword, ID);
        return userRepository.save(newUser); // 사용자 저장
    }

    // 사용자 로그인 메서드
    public User login(String ID, String password) {
        User user = userRepository.findByUserId(ID);
        if (user == null) {
            System.out.println("사용자를 찾을 수 없습니다: " + ID);
            return null; // 인증 실패
        }

        // 암호 검증을 Spring Security의 PasswordEncoder와 함께 수행
        if (passwordEncoder.matches(password, user.getPassword())) {
            // Spring Security의 Authentication 객체를 생성하고 SecurityContext에 설정
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUserId(), user.getPassword(), new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("로그인 성공: " + ID);
            return user; // 인증 성공
        } else {
            System.out.println("비밀번호 불일치: " + ID);
            return null; // 인증 실패
        }
    }

    // 관심 관광지 추가 메서드
    public void addFavoriteContent(String ID, String contentId, String contentTypeId) {
        User user = userRepository.findByUserId(ID);
        if (user != null) {
            // Map에 contentId와 contentTypeId를 추가
            user.getFavoriteContents().put(contentId, contentTypeId); // contentId를 키로 하고 contentTypeId를 값으로 저장
            userRepository.save(user); // 변경사항 저장
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + ID);
        }
    }
    // 관심 관광지 제거 메서드
    @Transactional
    public void removeFavoriteContent(String ID, String contentId) {
        User user = userRepository.findByUserId(ID);
        if (user != null) {
            user.removeFavoriteContent(contentId);
            userRepository.save(user); // 변경사항 저장
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + ID);
        }
    }

    // 관심 관광지 목록 조회 메서드
    @Transactional(readOnly = true)
    public Map<String, String> getFavoriteContents(String ID) {
        User user = userRepository.findByUserId(ID);
        if (user != null) {
            System.out.println("가져온 관심 관광지 목록: " + user.getFavoriteContents());
            return user.getFavoriteContents(); // Map<String, String> 반환
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + ID);
        }
    }

    @Transactional // 트랜잭션 추가
    public void saveFavoriteContent(User user, String contentId, String contentTypeId) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (contentId == null || contentId.isEmpty()) {
            throw new IllegalArgumentException("contentId cannot be null or empty");
        }
        if (contentTypeId == null || contentTypeId.isEmpty()) {
            throw new IllegalArgumentException("contentTypeId cannot be null or empty");
        }

        // 사용자 관심 관광지 추가
        user.addFavoriteContent(contentId, contentTypeId); // 관심 관광지 리스트에 추가

        // 저장 시 디버깅을 위한 로그
        System.out.println("Before saving user: " + user);

        userRepository.save(user); // 사용자 정보를 DB에 저장

        // 저장 후 디버깅을 위한 로그
        System.out.println("User saved with favorite content: " + contentId + " and contentTypeId: " + contentTypeId);
    }

    // 사용자 이름으로 사용자 찾기
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
