package app.app.TouristApi.Controller;

import app.app.TouristApi.DTO.TouristInfoWithAccessibilityDTO;
import app.app.TouristApi.Entity.AccessibleInfo;
import app.app.TouristApi.Service.TouristApiService;
import app.app.TouristApi.Service.TouristSpotService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/touristSpot")
public class TouristPageController {

    private final TouristApiService touristApiService;
    private final TouristSpotService touristSpotService;  // TouristSpotService 추가

    @Autowired
    public TouristPageController(TouristApiService touristApiService, TouristSpotService touristSpotService) {
        this.touristApiService = touristApiService;
        this.touristSpotService = touristSpotService;  // TouristSpotService 주입
    }

    @GetMapping("/area-search")
    public String getAreaSearchPage() {
        return "touristSpot/Area_Search"; // templates/touristSpot/Area_Search.html로 이동
    }

    @GetMapping("/tourist-information")
    public String getTouristInformation(
            @RequestParam String contentId,
            @RequestParam String contentTypeId,
            Model model) {

        TouristInfoWithAccessibilityDTO touristInfo = touristApiService.getTouristInfoWithAccessibility(contentId, contentTypeId);

        // 데이터를 모델에 추가하여 view로 넘깁니다.
        model.addAttribute("touristInfo", touristInfo);
        return "touristSpot/searchresult"; // searchresult.html로 이동
    }

    @GetMapping("/searchresult.html")
    public String getSearchResultPage(@RequestParam String contentId, Model model) {
        model.addAttribute("contentId", contentId);
        return "touristSpot/searchresult"; // templates/touristSpot/searchresult.html로 이동
    }


    // 무장애 검색 페이지 렌더링
    @GetMapping("/search-by-accessibility")
    public String getBarrierFreeSearchPage() {
        return "touristSpot/BarrierFreeSearch"; // BarrierFreeSearch.html을 렌더링
    }

}
