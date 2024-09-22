package app.app.course.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class RequestDTO {
    private int areaCode;
    private List<Integer> contentTypes;
    // Getters, Setters
}
