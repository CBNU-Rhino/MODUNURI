package app.app.course.Controller;

import app.app.course.Course;
import app.app.course.Service.CourseService;
import app.app.user.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller // @RestController에서 @Controller로 변경
@RequestMapping("/courses")
public class CourseRemoveController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/remove")
    public String removeCourse(@RequestParam("courseId") Long courseId,
                               @AuthenticationPrincipal CustomUserDetails user,
                               RedirectAttributes redirectAttributes) {
        Course course = courseService.getCourseById(courseId);

        // 삭제 권한 체크
        if (!course.getUser().getId().equals(user.getUser().getId())) {
            redirectAttributes.addFlashAttribute("error", "삭제할 권한이 없습니다.");
            return "redirect:/users/mypage";
        }

        // 코스 삭제
        courseService.deleteCourse(courseId);

        // 성공 메시지와 함께 MyPage로 리다이렉트
        redirectAttributes.addFlashAttribute("message", "코스가 성공적으로 삭제되었습니다.");
        return "redirect:/users/mypage"; // 리다이렉트
    }
}
