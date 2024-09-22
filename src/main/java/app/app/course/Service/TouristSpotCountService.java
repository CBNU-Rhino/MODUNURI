package app.app.course.Service;

import app.app.course.DTO.RequestDTO;
import app.app.course.RegionMapper;
import app.app.course.Repository.TouristSpotCountRepository;
import app.app.course.TouristSpotCount;
import jakarta.persistence.EntityManager;

import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class TouristSpotCountService {

    @Autowired
    private TouristSpotCountRepository touristSpotCountRepository;

    @Autowired
    private EntityManager entityManager;

    public List<TouristSpotCount> countTouristSpotsByRegion(RequestDTO request) {
        int areaCode = request.getAreaCode();
        List<Integer> contentTypes = request.getContentTypes();

        // IN절에 들어갈 contentTypes의 크기만큼 물음표(?)를 만듦
        String placeholders = String.join(",", contentTypes.stream().map(ct -> "?").toArray(String[]::new));

        // 동적으로 쿼리 생성
        String sql = "SELECT COUNT(*) FROM tourist_info WHERE area_code = ? AND content_type_id IN (" + placeholders + ")";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, areaCode);

        // 각 contentTypes 값을 순서대로 바인딩
        for (int i = 0; i < contentTypes.size(); i++) {
            query.setParameter(i + 2, contentTypes.get(i)); // 2번부터 content_type_id 값 바인딩
        }

        Long count = ((Number) query.getSingleResult()).longValue();
        String regionName = RegionMapper.getRegionName(areaCode);

        TouristSpotCount spotCount = new TouristSpotCount(regionName, count.intValue());
        List<TouristSpotCount> result = new ArrayList<>();
        result.add(spotCount);

        // TouristSpotCount 저장
        touristSpotCountRepository.save(spotCount);

        return result;
    }
}
