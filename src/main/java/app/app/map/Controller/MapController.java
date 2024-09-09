package app.app.map.Controller;

import app.app.user.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    // map.html 호출을 위한 GET 메서드
    @GetMapping("/touristSpot/map")
    public String showMapPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user != null) {
            // 로그인된 사용자 정보를 모델에 추가
            model.addAttribute("username", user.getUsername());
        } else {
            // 로그인되지 않은 경우, Guest 또는 기본 값으로 처리
            model.addAttribute("username", "Guest");
        }

        return "touristSpot/map";  // templates/touristSpot/map.html로 이동
    }
}