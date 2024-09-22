package app.app.TouristApi.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import app.app.TouristApi.Entity.AccessibleInfo;

import java.util.List;

@Repository
public interface AccessibleInfoRepository extends JpaRepository<AccessibleInfo, Long> {

    AccessibleInfo findByContentId(String contentId);

    boolean existsByContentId(String contentId);

    @Query("SELECT ai FROM AccessibleInfo ai WHERE ai.contentId IN :contentIdList")
    List<AccessibleInfo> findByContentIdIn(@Param("contentIdList") List<String> contentIdList);

    @Query("SELECT ai FROM AccessibleInfo ai WHERE ai.contentId IN :contentIdList AND (" +
            "(ai.wheelchair IS NOT NULL AND ai.wheelchair <> '' AND 'wheelchair' IN :accessibleTypeList) " +
            "OR (ai.elevator IS NOT NULL AND ai.elevator <> '' AND 'elevator' IN :accessibleTypeList) " +
            "OR (ai.restroom IS NOT NULL AND ai.restroom <> '' AND 'restroom' IN :accessibleTypeList) " +
            "OR (ai.brailleBlock IS NOT NULL AND ai.brailleBlock <> '' AND 'braille_block' IN :accessibleTypeList) " +
            "OR (ai.stroller IS NOT NULL AND ai.stroller <> '' AND 'stroller' IN :accessibleTypeList) " +
            "OR (ai.helpDog IS NOT NULL AND ai.helpDog <> '' AND 'help_dog' IN :accessibleTypeList)" +
            "OR (ai.blindHandicapEtc IS NOT NULL AND ai.blindHandicapEtc <> '' AND 'blind_handicap_etc' IN :accessibleTypeList))")
    List<AccessibleInfo> findByContentIdInAndAccessibleTypes(@Param("contentIdList") List<String> contentIdList,
                                                             @Param("accessibleTypeList") List<String> accessibleTypeList);

    // 무장애 정보가 제공되는지 확인하는 메서드 (필드가 빈칸이 아닌지 확인)
    @Query("SELECT COUNT(ai) > 0 FROM AccessibleInfo ai WHERE ai.contentId = :contentId AND ai.wheelchair IS NOT NULL AND ai.wheelchair <> ''")
    boolean existsByContentIdAndWheelchair(@Param("contentId") String contentId);

    @Query("SELECT COUNT(ai) > 0 FROM AccessibleInfo ai WHERE ai.contentId = :contentId AND ai.stroller IS NOT NULL AND ai.stroller <> ''")
    boolean existsByContentIdAndStroller(@Param("contentId") String contentId);

    @Query("SELECT COUNT(ai) > 0 FROM AccessibleInfo ai WHERE ai.contentId = :contentId AND ai.helpDog IS NOT NULL AND ai.helpDog <> ''")
    boolean existsByContentIdAndHelpDog(@Param("contentId") String contentId);

    @Query("SELECT COUNT(ai) > 0 FROM AccessibleInfo ai WHERE ai.contentId = :contentId AND ai.brailleBlock IS NOT NULL AND ai.brailleBlock <> ''")
    boolean existsByContentIdAndBrailleBlock(@Param("contentId") String contentId);

    @Query("SELECT COUNT(ai) > 0 FROM AccessibleInfo ai WHERE ai.contentId = :contentId AND ai.blindHandicapEtc IS NOT NULL AND ai.blindHandicapEtc <> ''")
    boolean existsByContentIdAndBlindHandicapEtc(@Param("contentId") String contentId);

    @Query("SELECT COUNT(ai) > 0 FROM AccessibleInfo ai WHERE ai.contentId = :contentId AND ai.elevator IS NOT NULL AND ai.elevator <> ''")
    boolean existsByContentIdAndElevator(@Param("contentId") String contentId);

}
