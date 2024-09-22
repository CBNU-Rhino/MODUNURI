package app.app.TouristApi.Repository;

import app.app.TouristApi.Entity.TouristInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TouristInfoRepository extends JpaRepository<TouristInfo, Long> {
    TouristInfo findByContentId(String contentId);
    // 기본적인 CRUD 메서드는 JpaRepository에서 제공됩니다.
    // 필요한 경우, 커스텀 쿼리 메서드를 추가할 수 있습니다.

    // 이미 존재하는지 확인하는 메서드 선언
    boolean existsByContentTypeIdAndAreaCodeAndSigunguCode(String contentTypeId, String areaCode, String sigunguCode);
    List<TouristInfo> findAllByAreaCode(String areaCode);
    boolean existsByContentId(String contentId);
    // 특정 지역과 시군구에 해당하는 관광지 조회
    List<TouristInfo> findByAreaCodeAndSigunguCodeIn(String areaCode, List<String> sigunguCodes);

    // 광역시의 경우 시군구 코드 전체를 조회
    List<TouristInfo> findByAreaCode(String areaCode);
}
