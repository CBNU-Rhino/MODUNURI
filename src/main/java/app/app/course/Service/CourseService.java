package app.app.course.Service;

import app.app.course.ContentInfo;
import app.app.course.Course;
import app.app.course.DTO.CourseDTO;
import app.app.course.Repository.CourseRepository;
import app.app.user.User;
import app.app.user.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    // 코스 추가
    public Course addCourse(Long userId, String courseName, List<Map<String, String>> contentData) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // contentId와 contentTypeId를 묶어서 ContentInfo 객체로 변환
        List<ContentInfo> contentInfos = contentData.stream()
                .map(data -> new ContentInfo(data.get("contentId"), data.get("contentTypeId")))
                .collect(Collectors.toList());

        // Course 생성 시 contentInfos 전달
        Course course = new Course(courseName, contentInfos, user);
        return courseRepository.save(course);
    }

    // 유저의 코스 목록 조회
    public List<CourseDTO> getUserCourses(Long userId) {
        List<Course> courses = courseRepository.findByUserId(userId);
        return courses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    // 코스 엔티티를 DTO로 변환하는 메서드
    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setContentInfos(course.getContentInfos());
        return dto;
    }
    // 코스 저장
    public Course save(Course course) {
        return courseRepository.save(course);
    }
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("코스를 찾을 수 없습니다."));
    }

}
