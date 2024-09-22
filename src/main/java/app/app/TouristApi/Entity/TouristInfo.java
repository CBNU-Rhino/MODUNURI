package app.app.TouristApi.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class TouristInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("contentid")
    private String contentId;         // 컨텐츠 ID

    @JsonProperty("contenttypeid")
    private String contentTypeId;     // 컨텐츠 타입 ID

    @JsonProperty("title")
    private String title;             // 제목

    @JsonProperty("createdtime")
    private String createdTime;       // 생성 시간

    @JsonProperty("modifiedtime")
    private String modifiedTime;      // 수정 시간

    @JsonProperty("tel")
    private String tel;               // 전화번호

    @JsonProperty("homepage")
    private String homepage;          // 홈페이지

    @JsonProperty("booktour")
    private String booktour;          // 북 투어 여부

    @JsonProperty("firstimage")
    private String firstImage;        // 첫 번째 이미지 URL

    @JsonProperty("firstimage2")
    private String firstImage2;       // 두 번째 이미지 URL

    @JsonProperty("cpyrhtDivCd")
    private String cpyrhtDivCd;       // 저작권 구분 코드

    @JsonProperty("areacode")
    private String areaCode;          // 지역 코드

    @JsonProperty("sigungucode")
    private String sigunguCode;       // 시군구 코드

    @JsonProperty("cat1")
    private String cat1;              // 대분류

    @JsonProperty("cat2")
    private String cat2;              // 중분류

    @JsonProperty("cat3")
    private String cat3;              // 소분류

    @JsonProperty("addr1")
    private String addr1;             // 주소 1

    @JsonProperty("addr2")
    private String addr2;             // 주소 2

    @JsonProperty("zipcode")
    private String zipcode;           // 우편번호

    @JsonProperty("mapx")
    private Double mapx;              // 지도 X좌표

    @JsonProperty("mapy")
    private Double mapy;              // 지도 Y좌표

    @JsonProperty("overview")
    private String overview;          // 개요
    // Getter와 Setter 추가

}
