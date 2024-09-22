package app.app.Home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import app.app.user.CustomUserDetails;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        System.out.println("Home method called");  // 메서드 호출 여부 확인
        if (user != null) {
            System.out.println("Logged in user: " + user.getUsername());  // 사용자 이름 출력
            model.addAttribute("username", user.getRealUsername());
        } else {
            System.out.println("No user logged in");
            model.addAttribute("username", null);  // 비로그인 시 null로 설정
        }
        return "index";  // resources/templates/index.html 반환
    }


}
