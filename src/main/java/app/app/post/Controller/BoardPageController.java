package app.app.post.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//board.html 반환을 위한 파일
@Controller
public class BoardPageController {

    @GetMapping("/community")
    public String getCommunityPage() {
        return "users/board";  // src/main/resources/templates/board.html이 반환됨
    }
}
