package app.app.course.DTO;

import app.app.course.ContentInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CourseDTO {
    private Long id;
    private String courseName;
    private List<ContentInfo> contentInfos;
}
