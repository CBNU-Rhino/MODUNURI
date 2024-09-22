package app.app.TouristApi.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessibilityRequestDTO {
    private String region;                // 선택한 지역 (예: 서울특별시)
    private String accessibilityFeature;  // 선택한 무장애 정보 (예: wheelchair, stroller)
}
