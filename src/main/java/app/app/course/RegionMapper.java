package app.app.course;

public class RegionMapper {

    public static String getRegionName(int areaCode) {
        switch (areaCode) {
            case 1:
                return "서울특별시";
            case 2:
                return "인천광역시";
            case 3:
                return "대전광역시";
            case 4:
                return "대구광역시";
            case 5:
                return "광주광역시";
            case 6:
                return "부산광역시";
            case 7:
                return "울산광역시";
            default:
                return "알 수 없는 지역";
        }
    }
}
