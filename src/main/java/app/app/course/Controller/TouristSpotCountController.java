package app.app.course.Controller;

import app.app.course.DTO.RequestDTO;
import app.app.course.Service.TouristSpotCountService;
import app.app.course.TouristSpotCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class TouristSpotCountController {

    @Autowired
    private TouristSpotCountService touristSpotCountService;

    @PostMapping("/count-tourist-spots")
    public List<TouristSpotCount> countTouristSpots(@RequestBody RequestDTO request) {
        try {
            return touristSpotCountService.countTouristSpotsByRegion(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error counting tourist spots", e);
        }
    }

}
