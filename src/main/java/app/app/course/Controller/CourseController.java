package app.app.course.Controller;

import app.app.course.ContentInfo;
import app.app.course.Course;
import app.app.course.Service.CourseService;
import app.app.user.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // 유저별 코스 조회
    @GetMapping
    public List<Course> getUserCourses(@AuthenticationPrincipal CustomUserDetails user) {
        return courseService.getUserCourses(user.getUser().getId());
    }

    @PostMapping("/courses/add")
    public ResponseEntity<?> addCourse(
            @RequestBody Map<String, Object> courseData,
            @AuthenticationPrincipal CustomUserDetails user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String courseName = (String) courseData.get("courseName");
        List<Map<String, String>> contentData = (List<Map<String, String>>) courseData.get("contentInfos");

        // 코스 저장 로직
        Course newCourse = courseService.addCourse(user.getUser().getId(), courseName, contentData);

        return ResponseEntity.ok("관심 코스가 저장되었습니다.");
    }

}
