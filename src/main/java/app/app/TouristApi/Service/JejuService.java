package app.app.TouristApi.Service;

import app.app.TouristApi.Entity.AccessibleInfo;
import app.app.TouristApi.Entity.TouristInfo;
import app.app.TouristApi.Repository.AccessibleInfoRepository;
import app.app.TouristApi.Repository.TouristInfoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Service
public class JejuService {

    private final TouristInfoRepository touristInfoRepository;
    private final AccessibleInfoRepository accessibleInfoRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String serviceKey = "fHhnNwA7fGBGdq%2FTX99FNNLQJh6pa3CQTHUPpKpk%2FyNHVqEzIDueYm2EKXOq7%2BfjY4fS4KpjCEQBoG3oQ0tTaQ%3D%3D";  // Replace with your actual service key

    public JejuService(TouristInfoRepository touristInfoRepository, AccessibleInfoRepository accessibleInfoRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.touristInfoRepository = touristInfoRepository;
        this.accessibleInfoRepository = accessibleInfoRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public void fetchAndSaveJejuAccessibilityInfo() {
        int jejuAreaCode = 37; // 제주도 지역 코드
        List<TouristInfo> jejuTouristInfos = touristInfoRepository.findAllByAreaCode(String.valueOf(jejuAreaCode));

        for (TouristInfo touristInfo : jejuTouristInfos) {
            String contentId = touristInfo.getContentId();

            // Check if accessibility info already exists to avoid duplicates
            if (accessibleInfoRepository.existsByContentId(contentId)) {
                continue;
            }

            // Fetch accessibility information for the tourist spot
            String jsonResponse = getAccessibilityData(contentId);
            if (jsonResponse != null) {
                saveAccessibilityInfoToDB(contentId, jsonResponse);
            }
        }
    }

    private String getAccessibilityData(String contentId) {
        try {
            String url = String.format(
                    "https://apis.data.go.kr/B551011/KorWithService1/detailWithTour1" +
                            "?serviceKey=%s&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest&contentId=%s&_type=json",
                    serviceKey, contentId
            );

            return restTemplate.getForObject(new URI(url), String.class);

        } catch (Exception e) {
            // Log the error and return null in case of any issue
            System.out.println("Error occurred while fetching accessibility data: " + e.getMessage());
            return null;
        }
    }

    private void saveAccessibilityInfoToDB(String contentId, String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode accessibilityNode = rootNode.path("response").path("body").path("items").path("item").get(0);

            // Map the JSON response to the AccessibleInfo entity
            AccessibleInfo accessibleInfo = objectMapper.treeToValue(accessibilityNode, AccessibleInfo.class);
            accessibleInfo.setContentId(contentId);  // Set the related content ID

            // Save the accessible information to the database
            accessibleInfoRepository.save(accessibleInfo);

        } catch (Exception e) {
            System.out.println("Error saving accessibility data: " + e.getMessage());
        }
    }

    public void fetchAndSaveJejuTouristInfo() {
        int jejuAreaCode = 37; // 제주도 지역 코드
        int jejusiCode = 14; // 제주시 시군구 코드
        int[] contentTypeIds = {12, 14, 15, 25, 28, 32, 38, 39}; // 순회할 관광 타입

        for (int contentTypeId : contentTypeIds) {
            // API 호출하여 관광지 정보 가져오기
            String jsonResponse = getTouristData(contentTypeId, jejuAreaCode, jejusiCode);

            // 응답이 있으면 데이터베이스에 저장
            if (jsonResponse != null) {
                saveTouristDataToDB(jsonResponse);
            }
        }
    }
    public String getTouristData(int contentTypeId, int areaCode, int sigunguCode) {
        try {
            // API URL 수정 (areaCode, sigunguCode, contentTypeId를 사용)
            String url = String.format(
                    "https://apis.data.go.kr/B551011/KorWithService1/areaBasedList1" +
                            "?serviceKey=%s&numOfRows=100&pageNo=1&MobileOS=ETC&MobileApp=AppTest" +
                            "&listYN=Y&arrange=C&_type=json&contentTypeId=%d&areaCode=%d&sigunguCode=%d",
                    serviceKey, contentTypeId, areaCode, sigunguCode);

            // API 호출하여 JSON 응답 받기
            RestTemplate restTemplate = new RestTemplate();
            String jsonResponse = restTemplate.getForObject(new URI(url), String.class);

            return jsonResponse;

        } catch (Exception e) {
            // 오류 처리
            System.out.println("Error fetching tourist data: " + e.getMessage());
            return null;
        }
    }


    public void saveTouristDataToDB(String jsonResponse) {
        try {
            // JSON 응답을 파싱하여 TouristInfo 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            for (JsonNode itemNode : itemsNode) {
                TouristInfo touristInfo = objectMapper.treeToValue(itemNode, TouristInfo.class);

                // 중복 확인 (이미 저장된 관광지인지 체크)
                boolean exists = touristInfoRepository.existsByContentId(touristInfo.getContentId());
                if (!exists) {
                    // TouristInfo DB에 저장
                    touristInfoRepository.save(touristInfo);
                }
            }
        } catch (Exception e) {
            System.out.println("Error saving tourist data to database: " + e.getMessage());
        }
    }


}
