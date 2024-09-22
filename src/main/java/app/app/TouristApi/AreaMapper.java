package app.app.TouristApi;


import app.app.Code.Area;

import java.util.ArrayList;
import java.util.List;

public class AreaMapper {

    // 광역시인지 확인하는 메서드
    public static boolean isMetropolitanCity(String regionName) {
        List<String> metropolitanCities = List.of("서울특별시", "부산광역시", "대구광역시", "인천광역시",
                "광주광역시", "대전광역시", "울산광역시", "세종특별자치시");
        return metropolitanCities.contains(regionName);
    }

    // 광역시의 지역 코드를 반환하는 메서드
    public static String getMetropolitanAreaCodeByRegionName(String regionName) {
        switch (regionName) {
            case "서울특별시":
                return "1";
            case "부산광역시":
                return "6";
            case "대구광역시":
                return "4";
            case "인천광역시":
                return "2";
            case "광주광역시":
                return "5";
            case "대전광역시":
                return "3";
            case "울산광역시":
                return "7";
            case "세종특별자치시":
                return "8";
            default:
                return null; // 광역시가 아니면 null 반환
        }
    }

    // 광역시의 모든 시군구 코드를 반환하는 메서드
    public static List<String> getSigunguCodesForMetropolitan(String areaCode) {
        List<String> sigunguCodes = new ArrayList<>();
        for (Area area : Area.values()) {
            if (String.valueOf(area.getRegionCode()).equals(areaCode)) {
                sigunguCodes.add(area.getSigunguCode());
            }
        }
        return sigunguCodes;
    }

    // 비광역시(일반 도)의 지역 코드를 반환하는 메서드
    public static String getAreaCodeByRegionName(String regionName) {
        for (Area area : Area.values()) {
            if (area.getRegionName().equals(regionName)) {
                return String.valueOf(area.getRegionCode());
            }
        }
        return null; // 해당 지역을 찾지 못했을 경우 null 반환
    }

    // 비광역시의 시군구 코드를 반환하는 메서드
    public static List<String> getSigunguCodesForNonMetropolitan(String regionName) {
        List<String> sigunguCodes = new ArrayList<>();
        for (Area area : Area.values()) {
            if (area.getRegionName().equals(regionName)) {
                sigunguCodes.add(area.getSigunguCode());
            }
        }
        return sigunguCodes;
    }
}
