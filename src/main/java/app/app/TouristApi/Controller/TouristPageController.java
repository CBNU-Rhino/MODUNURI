package app.app.TouristApi.Controller;

import app.app.TouristApi.DTO.TouristInfoWithAccessibilityDTO;
import app.app.TouristApi.Service.TouristApiService;
import app.app.TouristApi.Service.TouristSpotService;
import app.app.user.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

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
    public String getAreaSearchPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user != null) {
            model.addAttribute("username", user.getRealUsername());  // 로그인한 사용자의 이름을 모델에 추가
        }
        return "touristSpot/Area_Search"; // templates/touristSpot/Area_Search.html로 이동
    }


    @GetMapping("/tourist-information")
    public String getTouristInformation(
            @RequestParam String contentId,
            @RequestParam String contentTypeId,
            @AuthenticationPrincipal CustomUserDetails user, // 로그인한 사용자 정보 추가
            Model model) {

        // 관광지 정보와 접근성 정보를 가져옵니다.
        TouristInfoWithAccessibilityDTO touristInfo = touristApiService.getTouristInfoWithAccessibility(contentId, contentTypeId);

        // 로그인된 사용자가 있는 경우 사용자 정보를 모델에 추가
        if (user != null) {
            model.addAttribute("username", user.getRealUsername());
        } else {
            model.addAttribute("username", null); // 비로그인 상태일 때
        }

        // 관광지 정보를 모델에 추가하여 view로 넘깁니다.
        model.addAttribute("touristInfo", touristInfo);

        return "touristSpot/searchresult"; // searchresult.html로 이동
    }

    @GetMapping("/searchresult.html")
    public String getSearchResultPage(@RequestParam String contentId, @AuthenticationPrincipal CustomUserDetails user, Model model) {
        // 로그인된 사용자가 있는지 확인
        if (user != null) {
            // 로그인한 사용자의 이름을 모델에 추가
            model.addAttribute("username", user.getRealUsername());
        } else {
            // 로그인하지 않은 경우 null 처리
            model.addAttribute("username", null);
        }

        // contentId를 모델에 추가
        model.addAttribute("contentId", contentId);

        return "touristSpot/searchresult"; // templates/touristSpot/searchresult.html로 이동
    }


    // 무장애 검색 페이지 렌더링
    @GetMapping("/search-by-accessibility")
    public String getBarrierFreeSearchPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user != null) {
            model.addAttribute("username", user.getRealUsername());
        }
        return "touristSpot/BarrierFreeSearch"; // touristSpot/BarrierFreeSearch.html로 이동
    }

    // GET 요청을 처리하고 Travelplan.html을 반환
    @GetMapping("/travelplan")
    public String showTravelPlanPage(@AuthenticationPrincipal CustomUserDetails user, Model model, HttpServletResponse response) throws IOException {
        // 로그인된 사용자가 있을 경우, 사용자 이름을 모델에 추가
        if (user != null) {
            System.out.println("Logged in user: " + user.getUsername());  // 사용자 이름 출력
            model.addAttribute("username", user.getRealUsername());  // 사용자 이름을 모델에 추가
        } else {
            System.out.println("No user logged in");
            response.sendRedirect("/users/login");
            return null;  // 리다이렉트 후에는 null을 반환하여 다른 처리를 막음
        }

        return "touristSpot/Travelplan";  // templates/touristSpot/Travelplan.html 파일 반환
    }

}