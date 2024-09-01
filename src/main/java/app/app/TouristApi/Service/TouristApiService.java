package app.app.TouristApi.Service;

import app.app.Code.Area;
import app.app.TouristApi.DTO.AccessibilityInfoDTO;
import app.app.TouristApi.DTO.TouristInfoWithAccessibilityDTO;
import app.app.TouristApi.Repository.AccessibleInfoRepository;
import app.app.TouristApi.DTO.TouristDetailDTO;
import app.app.TouristApi.Entity.AccessibleInfo;
import app.app.TouristApi.Entity.TouristInfo;
import app.app.TouristApi.Repository.TouristInfoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class TouristApiService {

    private static final Logger logger = LoggerFactory.getLogger(TouristApiService.class);
    private final RestTemplate restTemplate;
    private final TouristInfoRepository touristInfoRepository;
    private final AccessibleInfoRepository accessibleInfoRepository;
    private final ObjectMapper objectMapper;
    private final String serviceKey = "A8noyzCmlZ5yEUXuX2yyMHEsfDbXIwWcrwZlNoBGxXgTf86J85h9JmanpHqY93UYP6tbzd1WbX0SLAMeAC1cdA%3D%3D";
    public TouristApiService(RestTemplate restTemplate, TouristInfoRepository touristInfoRepository, AccessibleInfoRepository accessibleInfoRepository, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.touristInfoRepository = touristInfoRepository;
        this.accessibleInfoRepository = accessibleInfoRepository;
        this.objectMapper = objectMapper;
    }

    public String getTouristData(int contentTypeId, int areaCode, int sigunguCode) {
        try {
            String url = String.format(
                    "https://apis.data.go.kr/B551011/KorWithService1/areaBasedList1" +
                            "?serviceKey=%s&numOfRows=100&pageNo=1&MobileOS=ETC&MobileApp=AppTest" +
                            "&listYN=Y&arrange=C&_type=json&contentTypeId=%d&areaCode=%d&sigunguCode=%d",
                    serviceKey, contentTypeId, areaCode, sigunguCode);

            URI uri = new URI(url);
            String jsonResponse = restTemplate.getForObject(uri, String.class);

            if (jsonResponse != null && jsonResponse.startsWith("<")) {
                logger.error("Received an XML response instead of JSON. Likely an API key issue.");
                logger.error("Response: {}", jsonResponse);
                return null;
            }

            return jsonResponse;

        } catch (URISyntaxException e) {
            logger.error("Invalid URI Syntax", e);
            return null;
        } catch (Exception e) {
            logger.error("Error occurred during API request", e);
            return null;
        }
    }

    public Area getAreaByRegionName(String regionName) {
        for (Area area : Area.values()) {
            if (area.getRegionName().equalsIgnoreCase(regionName)) {
                return area;
            }
        }
        return null; // 지역명을 찾지 못한 경우
    }

    public String getTouristDataByRegionName(String regionName, int contentTypeId) {
        try {
            Area area = getAreaByRegionName(regionName);
            if (area == null) {
                logger.error("Invalid region name: " + regionName);
                return null;
            }

            int areaCode = area.getRegionCode();
            int sigunguCode = Integer.parseInt(area.getSigunguCode());

            return getTouristData(contentTypeId, areaCode, sigunguCode);

        } catch (Exception e) {
            logger.error("Error fetching tourist data by region name", e);
            return null;
        }
    }

    public String getTouristDataByRegionAndSigungu(int contentTypeId, String regionName, String sigunguName) {
        int regionCode = getRegionCodeByRegionName(regionName);
        String sigunguCode = getSigunguCode(regionCode, sigunguName);

        if (regionCode == -1) {
            logger.error("Invalid region name: " + regionName);
            return null;
        }

        if (sigunguCode == null) {
            logger.error("Invalid sigungu name: " + sigunguName);
            return null;
        }

        return getTouristData(contentTypeId, regionCode, Integer.parseInt(sigunguCode));
    }

    private int getRegionCodeByRegionName(String regionName) {
        switch (regionName) {
            case "서울특별시":
                return 1;
            case "인천광역시":
                return 2;
            case "대전광역시":
                return 3;
            case "대구광역시":
                return 4;
            case "광주광역시":
                return 5;
            case "부산광역시":
                return 6;
            case "울산광역시":
                return 7;
            case "경기도":
                return 31;
            case "강원도":
                return 32;
            case "충청북도":
                return 33;
            case "충청남도":
                return 34;
            case "경상북도":
                return 35;
            case "경상남도":
                return 36;
            case "전라북도":
                return 37;
            case "전라남도":
                return 38;
            case "제주도":
                return 39;
            default:
                return -1; // 유효하지 않은 지역명
        }
    }

    private String getSigunguCode(int regionCode, String sigunguName) {
        for (Area enumArea : Area.values()) {
            if (enumArea.getRegionCode() == regionCode && enumArea.getRegionName().equalsIgnoreCase(sigunguName)) {
                return enumArea.getSigunguCode();
            }
        }
        return null;
    }


    public TouristInfoWithAccessibilityDTO getTouristInfoWithAccessibility(String contentId, String contentTypeId) {
        try {
            // 관광지 정보 가져오기
            String encodedContentId = URLEncoder.encode(contentId, StandardCharsets.UTF_8.toString());
            String encodedContentTypeId = URLEncoder.encode(contentTypeId, StandardCharsets.UTF_8.toString());
            String urlTourist = String.format(
                    "https://apis.data.go.kr/B551011/KorWithService1/detailCommon1" +
                            "?serviceKey=%s&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest" +
                            "&contentId=%s&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y" +
                            "&_type=json&contentTypeId=%s",
                    serviceKey, encodedContentId, encodedContentTypeId);

            String jsonResponseTourist = restTemplate.getForObject(new URI(urlTourist), String.class);
            JsonNode rootNodeTourist = objectMapper.readTree(jsonResponseTourist);
            JsonNode itemNodeTourist = rootNodeTourist.path("response").path("body").path("items").path("item").get(0);
            TouristDetailDTO touristDetail = objectMapper.treeToValue(itemNodeTourist, TouristDetailDTO.class);

            // 무장애 정보 가져오기
            String urlAccessibility = String.format(
                    "https://apis.data.go.kr/B551011/KorWithService1/detailWithTour1" +
                            "?serviceKey=%s&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest" +
                            "&contentId=%s&_type=json",
                    serviceKey, encodedContentId);

            String jsonResponseAccessibility = restTemplate.getForObject(new URI(urlAccessibility), String.class);
            JsonNode rootNodeAccessibility = objectMapper.readTree(jsonResponseAccessibility);
            JsonNode itemNodeAccessibility = rootNodeAccessibility.path("response").path("body").path("items").path("item").get(0);
            AccessibilityInfoDTO accessibilityInfo = objectMapper.treeToValue(itemNodeAccessibility, AccessibilityInfoDTO.class);

            // 두 정보를 합쳐서 반환
            TouristInfoWithAccessibilityDTO result = new TouristInfoWithAccessibilityDTO();
            result.setTouristDetail(touristDetail);
            result.setAccessibilityInfo(accessibilityInfo);
            return result;

        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
            return null;
        }
    }

    public void fetchAndSaveTouristData() {
        int[] contentTypes = {12, 14, 15, 25, 28, 32, 38, 39};

        for (int contentTypeId : contentTypes) {
            for (Area area : Area.values()) {
                // 중복 확인
                boolean exists = touristInfoRepository.existsByContentTypeIdAndAreaCodeAndSigunguCode(
                        String.valueOf(contentTypeId),
                        String.valueOf(area.getRegionCode()),
                        area.getSigunguCode()
                );

                if (!exists) {
                    String jsonResponse = getTouristData(contentTypeId, area.getRegionCode(), Integer.parseInt(area.getSigunguCode()));
                    if (jsonResponse != null) {
                        saveTouristDataToDB(jsonResponse);
                    }
                }
            }
        }
    }


    public void saveTouristDataToDB(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            for (JsonNode itemNode : itemsNode) {
                TouristInfo touristInfo = objectMapper.treeToValue(itemNode, TouristInfo.class);
                touristInfoRepository.save(touristInfo);
            }
        } catch (Exception e) {
            logger.error("Error saving tourist data to database", e);
        }
    }

    public void fetchAndSaveAccessibilityInfo() {
        // TouristInfo 테이블에서 모든 데이터 조회
        List<TouristInfo> touristInfos = touristInfoRepository.findAll();

        for (TouristInfo touristInfo : touristInfos) {
            String contentId = touristInfo.getContentId();

            // accessible_info 테이블에 이미 contentId가 있는지 확인
            if (accessibleInfoRepository.existsByContentId(contentId)) {
                // 이미 존재하면 API 호출을 생략하고 다음 contentId로 이동
                continue;
            }

            // 존재하지 않으면 API 호출
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
                    serviceKey, contentId);

            URI uri = new URI(url);
            String jsonResponse = restTemplate.getForObject(uri, String.class);

            if (jsonResponse != null && jsonResponse.startsWith("<")) {
                logger.error("Received an XML response instead of JSON. Likely an API key issue.");
                logger.error("Response: {}", jsonResponse);
                return null;
            }

            return jsonResponse;

        } catch (URISyntaxException e) {
            logger.error("Invalid URI Syntax", e);
            return null;
        } catch (Exception e) {
            logger.error("Error occurred during API request", e);
            return null;
        }
    }

    private void saveAccessibilityInfoToDB(String contentId, String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item").get(0);

            AccessibleInfo accessibilityInfo = objectMapper.treeToValue(itemsNode, AccessibleInfo.class);
            accessibilityInfo.setContentId(contentId);  // 연관 contentId 설정

            accessibleInfoRepository.save(accessibilityInfo);
        } catch (Exception e) {
            logger.error("Error saving accessibility info to database", e);
        }
    }
}
