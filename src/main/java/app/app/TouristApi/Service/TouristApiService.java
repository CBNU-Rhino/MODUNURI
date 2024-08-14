package app.app.TouristApi.Service;

import app.app.Code.Area;
import app.app.TouristApi.Repository.AccessibleInfoRepository;
import app.app.TouristApi.DTO.TouristDetailDTO;
import app.app.TouristApi.Entity.AccessibleInfo;
import app.app.TouristApi.Entity.TouristInfo;
import app.app.TouristApi.Repository.TouristInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.net.URISyntaxException;

@Service
public class TouristApiService {

    private static final Logger logger = LoggerFactory.getLogger(TouristApiService.class);

    private final RestTemplate restTemplate;
    private final TouristInfoRepository touristInfoRepository;
    private final AccessibleInfoRepository accessibleInfoRepository;
    private final ObjectMapper objectMapper;
    private final String serviceKey = "fHhnNwA7fGBGdq%2FTX99FNNLQJh6pa3CQTHUPpKpk%2FyNHVqEzIDueYm2EKXOq7%2BfjY4fS4KpjCEQBoG3oQ0tTaQ%3D%3D"; // 실제 API 키로 변경

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
                            "?serviceKey=%s&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest" +
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

    public TouristDetailDTO getTouristDetails(String contentId) {
        TouristInfo touristInfo = touristInfoRepository.findByContentId(contentId);
        AccessibleInfo accessibleInfo = accessibleInfoRepository.findByContentId(contentId);

        TouristDetailDTO detailDTO = new TouristDetailDTO();
        detailDTO.setTouristInfo(touristInfo);
        detailDTO.setAccessibleInfo(accessibleInfo);

        return detailDTO;
    }
}