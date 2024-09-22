package app.app.course.Repository;

import app.app.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // 유저별 코스 조회
    List<Course> findByUserId(Long userId);
}
