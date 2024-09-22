package app.app.TouristApi.Controller;

import app.app.TouristApi.DTO.TouristInfoDTO;
import app.app.TouristApi.DTO.TouristInfoWithAccessibilityDTO;
import app.app.TouristApi.Entity.AccessibleInfo;
import app.app.TouristApi.Entity.TouristInfo;
import app.app.TouristApi.Service.TouristApiService;
import app.app.TouristApi.Service.TouristSpotService;  // TouristSpotService 추가
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/touristSpot/Json")
public class TouristController {

    private final TouristApiService touristApiService;
    private final TouristSpotService touristSpotService;  // TouristSpotService 추가

    public TouristController(TouristApiService touristApiService, TouristSpotService touristSpotService) {
        this.touristApiService = touristApiService;
        this.touristSpotService = touristSpotService;  // TouristSpotService 주입
    }

    // 기존 코드: 지역 검색 페이지로 이동할 수 있도록 매핑
    @GetMapping("/area-search")
    public String getAreaSearchPage() {
        return "touristSpot/Area_Search"; // templates/touristSpot/Area_Search.html로 이동
    }

    // 기존 코드: 관광지 정보 조회 엔드포인트
    @GetMapping("/api/tourist-info")
    public ResponseEntity<List<TouristInfoDTO>> getTouristInfo(
            @RequestParam("region") String region,
            @RequestParam("sigungu") String sigungu,
            @RequestParam("contentTypeId") int contentTypeId) {

        String jsonResponse = touristApiService.getTouristDataByRegionAndSigungu(contentTypeId, region, sigungu);

        if (jsonResponse != null) {
            List<TouristInfoDTO> touristInfoList = parseTouristInfo(jsonResponse);
            return ResponseEntity.ok(touristInfoList);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private List<TouristInfoDTO> parseTouristInfo(String jsonResponse) {
        // JSON 응답을 TouristInfoDTO 리스트로 변환하는 로직 구현
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            List<TouristInfoDTO> touristInfoList = new ArrayList<>();
            for (JsonNode itemNode : itemsNode) {
                TouristInfoDTO info = objectMapper.treeToValue(itemNode, TouristInfoDTO.class);
                touristInfoList.add(info);
            }
            return touristInfoList;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 기존 코드: 관광지 무장애 정보 조회 엔드포인트
    @GetMapping("/tourist-information")
    public TouristInfoWithAccessibilityDTO getTouristInformation(
            @RequestParam String contentId,
            @RequestParam String contentTypeId) {

        return touristApiService.getTouristInfoWithAccessibility(contentId, contentTypeId);
    }


    // 새로운 무장애 관광지 정보 조회 API 추가
    @GetMapping("/accessible-tourist-spots")
    public ResponseEntity<List<TouristInfoDTO>> getAccessibleTouristSpots(
            @RequestParam("region") String region,
            @RequestParam("accessibleFeature") String accessibleFeature) {

        try {
            // TouristSpotService에서 무장애 관광지 정보를 가져옴
            List<TouristInfo> accessibleSpots = touristSpotService.getAccessibleTouristSpots(region, accessibleFeature);
            List<TouristInfoDTO> touristInfoDTOList = new ArrayList<>();

            // TouristInfo 엔티티를 TouristInfoDTO로 변환
            for (TouristInfo spot : accessibleSpots) {
                TouristInfoDTO dto = new TouristInfoDTO();
                dto.setContentId(spot.getContentId());
                dto.setTitle(spot.getTitle());
                dto.setAreaCode(spot.getAreaCode());
                dto.setAddr1(spot.getAddr1());
                dto.setFirstImage(spot.getFirstImage());
                dto.setContentTypeId(spot.getContentTypeId()); // 콘텐츠 타입 ID 추가
                dto.setMapX(spot.getMapx() != null ? spot.getMapx().toString() : null); // 지도 X좌표 추가
                dto.setMapY(spot.getMapy() != null ? spot.getMapy().toString() : null); // 지도 Y좌표 추가


                touristInfoDTOList.add(dto);
            }

            return ResponseEntity.ok(touristInfoDTOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
