package app.app.TouristApi.Service;

import app.app.TouristApi.AreaMapper;
import app.app.TouristApi.Entity.AccessibleInfo;
import app.app.TouristApi.Entity.TouristInfo;
import app.app.TouristApi.Repository.AccessibleInfoRepository;
import app.app.TouristApi.Repository.TouristInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Slf4j
public class TouristSpotService {

    private final AccessibleInfoRepository accessibleInfoRepository;
    @Autowired
    private TouristInfoRepository touristInfoRepository;
    // Logger 선언
    private static final Logger logger = LoggerFactory.getLogger(TouristSpotService.class);

    @Autowired
    public TouristSpotService(AccessibleInfoRepository accessibleInfoRepository) {
        this.accessibleInfoRepository = accessibleInfoRepository;
    }



    public List<AccessibleInfo> searchTouristSpotsByAccessibility(List<String> contentIdList, List<String> accessibleTypeList) {
        if (accessibleTypeList == null || accessibleTypeList.isEmpty()) {
            // accessibleTypeList가 비어 있으면 무장애 필터 없이 검색 수행
            return accessibleInfoRepository.findByContentIdIn(contentIdList);
        }
        return accessibleInfoRepository.findByContentIdInAndAccessibleTypes(contentIdList, accessibleTypeList);
    }

    public List<TouristInfo> getAccessibleTouristSpots(String region, String accessibleFeature) {
        String areaCode;
        List<TouristInfo> touristSpots;

        // 광역시인지 확인
        if (AreaMapper.isMetropolitanCity(region)) {
            areaCode = AreaMapper.getMetropolitanAreaCodeByRegionName(region);
            logger.debug("광역시 확인: Region: " + region + ", AreaCode: " + areaCode);
            // 광역시에 포함된 모든 구의 관광지를 조회
            List<String> sigunguCodes = AreaMapper.getSigunguCodesForMetropolitan(areaCode);
            touristSpots = touristInfoRepository.findByAreaCodeAndSigunguCodeIn(areaCode, sigunguCodes);
        } else {
            //비광역시의 경우
            areaCode = AreaMapper.getAreaCodeByRegionName(region);
            if (areaCode == null) {
                throw new IllegalArgumentException("Invalid region: " + region);
            }
            logger.debug("Non-metropolitan Area - Region: " + region + ", AreaCode: " + areaCode);


            List<String> sigunguCodes = AreaMapper.getSigunguCodesForNonMetropolitan(region);
            touristSpots = touristInfoRepository.findByAreaCodeAndSigunguCodeIn(areaCode, sigunguCodes);

            return filterByAccessibleFeature(touristSpots, accessibleFeature);
        }

        // 무장애 기능에 따라 필터링
        return filterByAccessibleFeature(touristSpots, accessibleFeature);
    }

    private List<TouristInfo> filterByAccessibleFeature(List<TouristInfo> touristSpots, String accessibleFeature) {
        List<TouristInfo> filteredSpots = new ArrayList<>();
        for (TouristInfo spot : touristSpots) {
            boolean hasAccessibleFeature = false;

            // accessibleFeature에 따라 해당 무장애 기능을 체크
            switch (accessibleFeature.toLowerCase()) {
                case "wheelchair":
                    hasAccessibleFeature = accessibleInfoRepository.existsByContentIdAndWheelchair(spot.getContentId());
                    break;
                case "stroller":
                    hasAccessibleFeature = accessibleInfoRepository.existsByContentIdAndStroller(spot.getContentId());
                    break;
                case "elevator":
                    hasAccessibleFeature = accessibleInfoRepository.existsByContentIdAndElevator(spot.getContentId());
                    break;
                case "help_dog":
                    hasAccessibleFeature = accessibleInfoRepository.existsByContentIdAndHelpDog(spot.getContentId());
                    break;
                case "braille_block":
                    hasAccessibleFeature = accessibleInfoRepository.existsByContentIdAndBrailleBlock(spot.getContentId());
                    break;
                case "blind_handicap_etc":
                    hasAccessibleFeature = accessibleInfoRepository.existsByContentIdAndBlindHandicapEtc(spot.getContentId());
                    break;
                default:
                    continue;
            }

            if (hasAccessibleFeature) {
                filteredSpots.add(spot);
            }
        }
        return filteredSpots;
    }

}
