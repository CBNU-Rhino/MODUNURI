package app.app.user.Repository;

import app.app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 사용자 조회하는 메서드
    User findByUserId(String userId);
    // 추가: Username으로 사용자 찾기
    User findByUsername(String username);
}
