package app.app.Code;

import lombok.Getter;

@Getter
public enum Area {

    // 서울
    A111(1, "1", "강남구", 127.047377408384, 37.51733192586),
    A112(1, "2", "강동구", 127.146482086617, 37.5366932749787),
    A113(1, "3", "강북구", 127.013119283383, 37.6686914100331),
    A114(1, "4", "강서구", 126.822656825948, 37.5507313994017),
    A115(1, "5", "관악구", 126.951561853867, 37.4783683761407),
    A116(1, "6", "광진구", 127.085809539463, 37.5386222066154),
    A117(1, "7", "구로구", 126.889029659375, 37.495087324902),
    A118(1, "8", "금천구", 126.90387703245, 37.4514694912951),
    A119(1, "9", "노원구", 127.025538071854, 37.6397513275933),
    A120(1, "10", "도봉구", 127.056430475217, 37.6543617567106),
    A121(1, "11", "동대문구", 127.038732774808, 37.5744948547554),
    A122(1, "12", "동작구", 126.93931505634, 37.5124820423588),
    A123(1, "13", "마포구", 126.929048398308, 37.5663246650425),
    A124(1, "14", "서대문구", 126.9368156604, 37.5791618639849),
    A125(1, "15", "서초구", 127.037710542477, 37.5119511897691),
    A126(1, "16", "성동구", 127.042164971794, 37.5639603172553),
    A127(1, "17", "성북구", 127.020728748992, 37.589273497635),
    A128(1, "18", "송파구", 127.123792501253, 37.5301933196226),
    A129(1, "19", "양천구", 126.865705555039, 37.5241448043756),
    A130(1, "20", "영등포구", 126.896934653272, 37.5237203404422),
    A131(1, "21", "용산구", 126.966642647192, 37.5316468131969),
    A132(1, "22", "은평구", 126.93101547313, 37.6176138562385),
    A133(1, "23", "종로구", 126.979392281245, 37.5744184359353),
    A134(1, "24", "중구", 126.995962137086, 37.5639410284465),
    A135(1, "25", "중랑구", 127.094936708518, 37.595479579206),

    // 인천
    A211(2, "1", "강화군", 126.48801002173, 37.7465168211797),
    A212(2, "2", "계양구", 126.739374543361, 37.5384273691477),
    A213(2, "3", "남동구", 126.737645976436, 37.4492070129826),
    A214(2, "4", "동구", 126.642502492282, 37.4742965293452),
    A215(2, "5", "미추홀구", 126.651344839676, 37.463312098497),
    A216(2, "6", "부평구", 126.723245742758, 37.5073497686612),
    A217(2, "7", "서구", 126.675221844285, 37.5455196794776),
    A218(2, "8", "연수구", 126.678898887234, 37.410099573049),
    A219(2, "9", "옹진군", 126.622242051834, 37.4683014366187),
    A220(2, "10", "중구", 126.632841254182, 37.4742961299178),

    // 대전
    A311(3, "1", "대덕구", 127.42368812408, 36.3465453733963),
    A312(3, "2", "동구", 127.45048167396, 36.3337372148934),
    A313(3, "3", "서구", 127.359319687943, 36.3348982851413),
    A314(3, "4", "유성구", 127.320039217578, 36.3552534969118),
    A315(3, "5", "중구", 127.387675888346, 36.3083393015122),

    // 대구
    A411(4, "1", "남구", 128.593791527545, 35.8482538123478),
    A412(4, "2", "달서구", 128.533452667386, 35.8451865739486),
    A413(4, "3", "달성군", 128.419173313043, 35.8012927552732),
    A414(4, "4", "동구", 128.635713947651, 35.8822416966426),
    A415(4, "5", "북구", 128.607282246688, 35.8904459354721),
    A416(4, "6", "서구", 128.543347507932, 35.8779787617874),
    A417(4, "7", "수성구", 128.635713947651, 35.8796883512743),
    A418(4, "8", "중구", 128.60541706419, 35.8693162972228),

    // 광주
    A511(5, "1", "광산구", 126.908698939228, 35.1516073994782),
    A512(5, "2", "남구", 126.9024768687986, 35.1328197804147),
    A513(5, "3", "동구", 126.930013960329, 35.1438397990535),
    A514(5, "4", "북구", 126.890187526297, 35.1520722691738),
    A515(5, "5", "서구", 126.855477318489, 35.1486732512596),

    // 부산
    A611(6, "1", "강서구", 129.11312518910896, 35.145538378275056),
    A612(6, "2", "금정구", 129.02429624836347, 35.09792355764983),
    A613(6, "3", "기장군", 129.163596084022, 35.1630666685904),
    A614(6, "4", "남구", 129.092655441283, 35.0962746625428),
    A615(6, "5", "동구", 129.037406531982, 35.129042565007),
    A616(6, "6", "동래구", 129.086737330234, 35.2094058127236),
    A617(6, "7", "부산진구", 129.042248509515, 35.1566825181184),
    A618(6, "8", "북구", 129.031683056712, 35.1970785585373),
    A619(6, "9", "사상구", 129.032373132273, 35.106226983637),
    A620(6, "10", "사하구", 128.974932970713, 35.1044479031967),
    A621(6, "11", "서구", 129.015541384012, 35.1059282447593),
    A622(6, "12", "수영구", 129.067888802333, 35.0912398946385),
    A623(6, "13", "연제구", 129.07974139535176, 35.176205865206086),
    A624(6, "14", "영도구", 129.059135624006, 35.0927393407794),
    A625(6, "15", "중구", 129.035879762334, 35.1064302413585),
    A626(6, "16", "해운대구", 129.168854663177, 35.1651769822483),

    // 울산
    A711(7, "1", "남구", 129.330091790845, 35.543809703761),
    A712(7, "2", "동구", 129.4164855733705, 35.50463591923512),
    A713(7, "3", "북구", 129.361146675243, 35.5827469957671),
    A714(7, "4", "울주군", 129.24211289801443, 35.52202518974331),
    A715(7, "5", "중구", 129.332494084537, 35.5693352250797),

    // 경기도
    A3111(31, "1", "가평군", 127.509570182187, 37.831441310071),
    A3112(31, "2", "고양시", 126.832020864535, 37.6528222138327),
    A3113(31, "3", "과천시", 126.987661477496, 37.4292293990325),
    A3114(31, "4", "광명시", 126.85381546259, 37.4784295452435),
    A3115(31, "5", "광주시", 127.289228474417, 37.428762219441),
    A3116(31, "6", "구리시", 127.12959747291, 37.595608086435),
    A3117(31, "7", "군포시", 126.950275920475, 37.3645403321066),
    A3118(31, "8", "김포시", 126.716618106387, 37.6153906321695),
    A3119(31, "9", "남양주시", 127.177486679243, 37.2410661172494),
    A3120(31, "10", "동두천시", 127.061355695319, 37.9036504391953),
    A3121(31, "11", "부천시", 126.762609492158, 37.503790211515),
    A3122(31, "12", "성남시", 127.126242491333, 37.4198855350581),
    A3123(31, "13", "수원시", 127.028429218244, 37.2634685600163),
    A3124(31, "14", "시흥시", 126.742749272101, 37.3991521602433),
    A3125(31, "15", "안산시", 126.83085456607, 37.3218485875035),
    A3126(31, "16", "안성시", 127.27984704072, 37.0080820735151),
    A3127(31, "17", "안양시", 127.214860598564, 37.5392745653604),
    A3128(31, "18", "양주시", 127.105919032085, 37.7995671030473),
    A3129(31, "19", "양평군", 127.200332890653, 37.8949928340102),
    A3130(31, "20", "여주시", 127.636569006971, 37.2984233734636),
    A3131(31, "21", "연천군", 127.075351220883, 38.0965186516039),
    A3132(31, "22", "오산시", 127.206927387299, 37.1993924671123),
    A3133(31, "23", "용인시", 127.033899090338, 37.7380566430654),
    A3134(31, "24", "의왕시", 127.004219228064, 37.4317089269189),
    A3135(31, "25", "의정부시", 127.046956377563, 37.7383153495878),
    A3136(31, "26", "이천시", 127.436645916005, 37.272223014956),
    A3137(31, "27", "파주시", 126.779874582294, 37.76003417250035),
    A3138(31, "28", "평택시", 127.112688386547, 36.9922606745476),
    A3139(31, "29", "포천시", 127.248247440914, 37.8941369345673),
    A3140(31, "30", "하남시", 127.212729409588, 37.5384722888574),
    A3141(31, "31", "화성시", 126.831477350332, 37.1995372034835),

    // 강원도
    A3211(32, "1", "강릉시", 128.875906144843, 37.7521080804225),
    A3212(32, "2", "고성군", 128.467866303864, 38.3805924324258),
    A3213(32, "3", "동해시", 129.165111673952, 37.4498976008123),
    A3214(32, "4", "삼척시", 129.165197049975, 37.448236957265),
    A3215(32, "5", "속초시", 128.591948320515, 38.2068660935174),
    A3216(32, "6", "양구군", 127.991423972539, 38.1100093228358),
    A3217(32, "7", "양양군", 128.624836396454, 38.0754190680948),
    A3218(32, "8", "영월군", 128.463306796895, 37.1838297605997),
    A3219(32, "9", "원주시", 127.920097123186, 37.3420475880697),
    A3220(32, "10", "인제군", 128.45890664318, 37.9000970538631),
    A3221(32, "11", "정선군", 128.591938589235, 38.206894335257),
    A3222(32, "12", "철원군", 127.312557379756, 38.1467954660241),
    A3223(32, "13", "춘천시", 127.72349833143, 37.8813167423121),
    A3224(32, "14", "태백시", 128.985721328719, 37.1640040981527),
    A3225(32, "15", "평창군", 128.479147607426, 37.6612054232528),
    A3226(32, "16", "홍천군", 127.889548871435, 37.697171686316),
    A3227(32, "17", "화천군", 127.612591366174, 38.1062705424238),
    A3228(32, "18", "횡성군", 127.924493591985, 37.2069527408123),

    // 충청북도
    A3311(33, "1", "괴산군", 127.78665104247418, 36.81535716509145),
    A3312(33, "2", "단양군", 128.3655442185559, 36.98465776942097),
    A3313(33, "3", "보은군", 127.729498803343, 36.4894353521523),
    A3314(33, "4", "영동군", 127.925961035789, 36.9910490160367),
    A3315(33, "5", "옥천군", 127.43553384338794, 36.855379000059486),
    A3316(33, "6", "음성군", 127.690480041098, 36.9403218849274),
    A3317(33, "7", "제천시", 127.48901904390766, 36.642488039442746),
    A3318(33, "8", "증평군", 127.580853763352, 36.8065812083485),
    A3319(33, "10", "진천군", 127.442189420629, 36.8591564840551),
    A3320(33, "11", "청주시", 127.78665104247418, 36.81535716509145),
    A3321(33, "12", "충주시", 127.925961035789, 36.9910490160367),

    // 충청남도
    A3411(34, "16", "계룡시", 127.249765024316, 36.2746629834395),
    A3412(34, "1", "공주시", 127.1146502, 36.4464622),
    A3413(34, "2", "금산군", 127.24854256142, 36.274558995858),
    A3414(34, "3", "논산시", 127.080595, 36.1871381),
    A3415(34, "4", "당진시", 126.6438392, 36.894199),
    A3416(34, "5", "보령시", 126.6127593, 36.3323367),
    A3417(34, "6", "부여군", 126.911207, 36.274555),
    A3418(34, "7", "서산시", 126.4523444, 36.7853716),
    A3419(34, "8", "서천군", 126.693002009145, 36.0803204362893),
    A3420(34, "9", "아산시", 126.645888436836, 36.8896748628807),
    A3421(34, "11", "예산군", 126.8021895, 36.7908992),
    A3422(34, "12", "천안시", 127.119055, 36.4465551),
    A3423(34, "13", "청양군", 126.6448884, 36.4587278),
    A3424(34, "14", "태안군", 126.2978988, 36.7456024),
    A3425(34, "15", "홍성군", 126.6608136, 36.6009582),

    // 경상북도
    A3511(35, "1", "경산시", 128.74131437676053, 35.825081617561416),
    A3512(35, "2", "경주시", 129.224706, 35.8562149),
    A3513(35, "3", "고령군", 128.57281478661784, 36.24291308508081),
    A3514(35, "4", "구미시", 128.34424636107, 36.1195708487424),
    A3515(35, "5", "군위군", 128.262895757565, 35.726065320875),
    A3516(35, "6", "김천시", 128.113624475917, 36.1398881671927),
    A3517(35, "7", "문경시", 128.187049424025, 36.5958515395364),
    A3518(35, "8", "봉화군", 128.732386681877, 36.8931350959526),
    A3519(35, "9", "상주시", 128.200763, 36.5644388),
    A3520(35, "10", "성주군", 128.1802026, 35.9227983),
    A3521(35, "11", "안동시", 128.62393703546982, 36.80561927570791),
    A3522(35, "12", "영덕군", 129.3542125, 36.6646958),
    A3523(35, "13", "영양군", 128.15914507699947, 36.41093980020886),
    A3524(35, "14", "영주시", 128.73401526249253, 35.64738481498588),
    A3525(35, "15", "영천시", 129.112510246094, 36.6667028574383),
    A3526(35, "16", "예천군", 128.454563108147, 36.657628),
    A3527(35, "17", "울릉군", 130.9057128084862, 37.48444159320234),
    A3528(35, "18", "울진군", 129.4082285, 36.9897481),
    A3529(35, "19", "의성군", 128.696516, 36.3525278),
    A3530(35, "20", "청도군", 128.662473124615, 35.6476665953193),
    A3531(35, "21", "청송군", 129.0560135, 36.5848312),
    A3532(35, "22", "칠곡군", 128.3206721, 36.0326086),
    A3533(35, "23", "포항시", 129.343164578839, 36.0189954295148),

    // 경상남도 - 가나다 순
    A3611(36, "1", "거제시", 128.6216477, 34.9070342),
    A3612(36, "2", "거창군", 127.9093562, 35.6867194),
    A3613(36, "3", "고성군", 128.4064235, 35.2724068),
    A3614(36, "4", "김해시", 128.8560733, 35.2333777),
    A3615(36, "5", "남해군", 128.0695084, 34.8377015),
    A3616(36, "7", "밀양시", 128.7478434, 35.5034811),
    A3617(36, "8", "사천시", 128.0168786, 35.0045294),
    A3618(36, "9", "산청군", 127.892652, 35.190755),
    A3619(36, "10", "양산시", 129.037272788817, 35.3349920568695),  // 새롭게 추가된 양산시
    A3620(36, "12", "의령군", 128.408698, 35.1648583),
    A3621(36, "13", "진주시", 128.0840074, 35.1919185),
    A3622(36, "15", "창녕군", 128.4937512, 35.3129505),
    A3623(36, "16", "창원시", 128.6816797, 35.2279402),
    A3624(36, "17", "통영시", 128.4146567, 34.8467455),
    A3625(36, "18", "하동군", 127.746351, 35.1572621),
    A3626(36, "19", "함안군", 128.408698, 35.1648583),
    A3627(36, "20", "함양군", 127.7251605, 35.5205272),
    A3628(36, "21", "합천군", 128.1657908, 35.5666703),


    // 전라북도
    A3711(37, "1", "고창군", 126.712617, 35.4359197),
    A3712(37, "2", "군산시", 126.717916, 35.968003),
    A3713(37, "3", "김제시", 126.88073, 35.804998),
    A3714(37, "4", "남원시", 127.390033, 35.401333),
    A3715(37, "5", "무주군", 127.660806, 36.006844),
    A3716(37, "6", "부안군", 126.733114, 35.731656),
    A3717(37, "7", "순창군", 126.803479, 35.650401),
    A3718(37, "8", "완주군", 127.162097, 35.904853),
    A3719(37, "9", "익산시", 126.977911, 35.950912),
    A3720(37, "10", "임실군", 127.288029, 35.600203),
    A3721(37, "11", "장수군", 127.519875, 35.650271),
    A3722(37, "12", "전주시", 127.148102, 35.824225),
    A3723(37, "13", "정읍시", 126.855048, 35.570557),
    A3724(37, "14", "진안군", 127.424865, 35.791772),

    // 전라남도
    A3811(38, "1", "강진군", 126.76978, 34.643123),
    A3812(38, "2", "고흥군", 127.284846, 34.601673),
    A3813(38, "3", "곡성군", 127.293283, 35.283415),
    A3814(38, "4", "광양시", 127.572225, 34.973469),
    A3815(38, "5", "구례군", 127.467398, 35.202576),
    A3816(38, "6", "나주시", 126.717930, 34.616444),
    A3817(38, "7", "담양군", 126.982387, 35.322501),
    A3818(38, "8", "목포시", 126.394817, 34.811919),
    A3819(38, "9", "무안군", 126.482764, 34.598499),
    A3820(38, "10", "보성군", 127.079543, 34.743617),
    A3821(38, "11", "순천시", 127.014423, 34.950522),
    A3822(38, "12", "신안군", 126.048844, 34.829533),
    A3823(38, "13", "여수시", 127.662223, 34.753707),
    A3824(38, "14", "영광군", 126.507364, 35.277707),
    A3825(38, "15", "영암군", 126.696855, 34.887573),
    A3826(38, "16", "완도군", 127.125422, 34.311053),
    A3827(38, "17", "장성군", 126.744963, 35.300754),
    A3828(38, "18", "장흥군", 126.863608, 34.723236),
    A3829(38, "19", "진도군", 126.263044, 34.519375),
    A3830(38, "20", "함평군", 126.501303, 35.098274),
    A3831(38, "21", "해남군", 126.617388, 34.574217),
    A3832(38, "22", "화순군", 126.986747, 35.064508),

    // 제주도
    A3911(39, "3", "서귀포시", 126.5096066, 33.2538694),
    A3912(39, "4", "제주시", 126.5219345, 33.4890113);

    private final int regionCode;
    private final String sigunguCode;
    private final String regionName;
    private final double x;
    private final double y;

    Area(int regionCode, String sigunguCode, String regionName, double x, double y) {
        this.regionCode = regionCode;
        this.sigunguCode = sigunguCode;
        this.regionName = regionName;
        this.x = x;
        this.y = y;
    }

    /**
     * Array로 반환
     *
     * @param code
     * @return
     */
    public static double[] getCoordi(String code){
        double x = Area.valueOf(code).x;
        double y = Area.valueOf(code).y;
        return new double[]{x, y};
    }
}
