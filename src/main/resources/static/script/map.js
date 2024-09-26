// 모달 관련 변수 전역 선언
let modal, modalBackdrop, modalClose;

// 지도 및 관련 변수 초기화
var mapContainer = document.getElementById('map'), // HTML에서 'map'이라는 ID를 가진 요소를 찾음
    mapOption = {
        center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울 시청의 위도와 경도 좌표로 지도 중심 설정
        level: 8 // 지도 확대 수준 설정 (숫자가 클수록 지도가 더 멀리 보임)
    };

var map = new kakao.maps.Map(mapContainer, mapOption); // 카카오 지도 객체 생성
var scheduleRoutes = {}; // 관광지 경로를 저장할 객체, 키는 관광지 ID
var markers = {}; // 지도 위에 표시할 마커들을 저장할 객체, 관광지 ID로 관리
var currentPathPolyline = null; // 현재 경로를 표시하는 폴리라인 객체
var favoritePlaces = {}; // 사용자가 관심 등록한 관광지 정보를 저장하는 객체

// 저장된 관광지 목록 불러오기
function loadSavedPlaces() {
    fetch('/users/getFavoriteContents') // 서버에서 관심 관광지 목록을 가져옴
        .then(response => response.json()) // 응답을 JSON 형식으로 변환
        .then(favoritePlacesData => {
            favoritePlaces = favoritePlacesData; // 가져온 데이터로 favoritePlaces 객체를 업데이트
            Object.keys(favoritePlaces).forEach(contentId => { // 각 관광지 ID에 대해 정보를 가져옴
                const contentTypeId = favoritePlaces[contentId];
                fetch(`/touristSpot/Json/tourist-information?contentId=${contentId}&contentTypeId=${contentTypeId}`) // 관광지 상세 정보를 API에서 요청
                    .then(response => response.json())
                    .then(data => {
                        displaySavedPlace(data.touristDetail); // 관광지 정보를 화면에 표시
                    })
                    .catch(error => console.error("Error fetching tourist information:", error)); // 에러 처리
            });
        })
        .then(() => {
            initializeSortable(); // 저장된 목록이 불러온 후에 드래그 앤 드롭 기능 적용
        })
        .catch(error => console.error('Error loading saved places:', error)); // 에러 처리
}

// 저장된 관광지 정보를 박스로 표시
function displaySavedPlace(touristDetail) {
    const savedPlaces = document.querySelector('#saved-places'); // 저장된 관광지를 표시할 HTML 요소 선택
    const placeBox = document.createElement('div'); // 새로운 div 요소 생성
    placeBox.classList.add('place-box'); // 'place-box' 클래스를 추가
    placeBox.id = `place-${touristDetail.contentid}`; // 관광지 ID로 박스에 고유 ID를 부여

    // 관광지의 이미지나 기본 이미지 설정
    const imageUrl = touristDetail.firstimage || '/images/placeholder.jpg';
    const title = touristDetail.title || "제목 없음"; // 제목이 없을 경우 "제목 없음"으로 표시
    const address = touristDetail.addr1 || "주소 없음"; // 주소가 없을 경우 "주소 없음"으로 표시

    // 박스 내부에 HTML로 관광지 정보 추가
    placeBox.innerHTML = `
        <div class="place-info">
            <img src="${imageUrl}" alt="${title}" class="place-image" />
            <div class="place-text">
                <h4>${title}</h4>
                <p>${address}</p>
                <button onclick="openTouristModal('${touristDetail.contentid}', '${touristDetail.contenttypeid}')">상세정보</button>
            </div>
        </div>
    `;

    savedPlaces.appendChild(placeBox); // 생성된 박스를 savedPlaces 영역에 추가
}

// Sortable.js로 드래그 앤 드롭 기능 초기화
function initializeSortable() {
    const savedPlacesContainer = document.getElementById('saved-places');
    const scheduleContainer = document.getElementById('schedule');

    // Sortable 인스턴스 생성 (저장된 관광지 목록)
    Sortable.create(savedPlacesContainer, {
        group: {
            name: 'places',  // 그룹 이름 지정
            pull: true,      // 컨테이너에서 요소를 이동할 수 있음
            put: true        // 일정 컨테이너에서 다시 저장된 목록으로 이동 가능
        },
        animation: 150, // 드래그 시 애니메이션 속도 (ms)
        ghostClass: 'ghost', // 드래그 중인 요소에 추가되는 클래스
        onEnd: function (evt) {
            console.log('Element moved in saved places', evt.item); // 요소가 이동한 후 이벤트
        }
    });

    // Sortable 인스턴스 생성 (일정 컨테이너)
    Sortable.create(scheduleContainer, {
        group: {
            name: 'places',  // 같은 그룹으로 설정하여 서로 이동 가능
            pull: true,      // 일정 컨테이너에서 저장된 목록으로 다시 드래그 가능
            put: true        // 저장된 장소에서 일정으로 드롭 가능
        },
        animation: 150, // 드래그 시 애니메이션 속도 (ms)
        ghostClass: 'ghost', // 드래그 중인 요소에 추가되는 클래스
        onAdd: function (evt) { // 일정 컨테이너로 추가될 때 발생
            const placeId = evt.item.id.split('-')[1]; // 관광지 ID 추출
            console.log('Element added to schedule', placeId);

            // 관광지가 일정에 추가되면 마커와 경로를 추가
            fetch(`/touristSpot/Json/tourist-information?contentId=${placeId}&contentTypeId=${favoritePlaces[placeId]}`)
                .then(response => response.json())
                .then(data => {
                    addMarkerAndRoute(data.touristDetail); // 마커 및 경로 추가
                    const coords = new kakao.maps.LatLng(data.touristDetail.mapy, data.touristDetail.mapx);
                    map.setCenter(coords); // 지도 중심을 해당 관광지의 좌표로 이동
                })
                .catch(error => console.error("Error fetching tourist information:", error));
        },
        onRemove: function (evt) { // 일정 컨테이너에서 항목이 제거될 때 발생
            const placeId = evt.item.id.split('-')[1]; // 관광지 ID 추출
            console.log('Element removed from schedule', placeId);

            // 일정 목록에서 제거되면 마커 삭제
            removeMarker(placeId);
        },
        onEnd: function (evt) {
            console.log('Element position changed within schedule', evt.item); // 일정 내에서 위치가 변경된 경우
        }
    });
}


// 오버레이를 저장할 객체 추가
var overlays = {}; // 지도 위에 표시할 오버레이들을 저장할 객체, 관광지 ID로 관리

// 마커 및 경로 추가 함수
function addMarkerAndRoute(touristDetail) {
    // 관광지의 좌표 (위도와 경도)를 가져옴
    const coords = new kakao.maps.LatLng(touristDetail.mapy, touristDetail.mapx);
    const placeId = touristDetail.contentid; // 관광지의 고유 ID

    // 마커 생성 (지도에 표시할 기본 마커)
    const marker = new kakao.maps.Marker({
        position: coords,  // 마커가 표시될 위치 (관광지 좌표)
        map: map,  // 마커를 표시할 지도 객체 (카카오 지도)
    });

    // 마커 위에 관광지 이름을 표시하는 커스텀 오버레이 내용 (HTML)
    const overlayContent = `
        <div style="background-color: white; border: 1px solid #ccc; padding: 5px 10px; border-radius: 5px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); font-size: 12px; font-weight: bold;">
            ${touristDetail.title}  <!-- 관광지 제목 표시 -->
        </div>
    `;

    // 커스텀 오버레이 생성 (마커 위에 관광지 이름 표시)
    const customOverlay = new kakao.maps.CustomOverlay({
        position: coords,  // 오버레이가 표시될 위치 (마커와 동일한 좌표)
        content: overlayContent,  // 표시할 HTML 내용 (관광지 이름)
        yAnchor: 1.5,  // yAnchor로 오버레이 위치를 조정 (마커 위에 배치)
        map: map,  // 커스텀 오버레이를 표시할 지도 객체
    });

    // 경로와 마커, 오버레이 정보를 scheduleRoutes, markers, overlays 객체에 저장
    scheduleRoutes[placeId] = coords;  // 경로 정보 저장 (관광지 좌표)
    markers[placeId] = marker;  // 마커 정보 저장
    overlays[placeId] = customOverlay;  // 오버레이 정보 저장

    // 마커를 클릭했을 때 실행되는 이벤트 리스너 추가 (관광지 상세 모달 표시)
    kakao.maps.event.addListener(marker, 'click', function() {
        openTouristModal(touristDetail.contentid, touristDetail.contenttypeid);  // 모달 열기 함수 호출
    });
}

// 마커 및 오버레이 제거 함수
function removeMarker(placeId) {
    // 마커가 존재하면 지도에서 제거
    if (markers[placeId]) {
        markers[placeId].setMap(null); // 지도에서 마커 제거
        delete markers[placeId]; // 마커 객체에서 삭제
    }

    // 오버레이가 존재하면 지도에서 제거
    if (overlays[placeId]) {
        overlays[placeId].setMap(null); // 지도에서 오버레이 제거
        delete overlays[placeId]; // 오버레이 객체에서 삭제
    }

    // 경로 정보도 삭제
    delete scheduleRoutes[placeId];
}

// 자동차 경로를 가져오는 함수 (카카오 내비 API 사용)
async function getCarDirection() {
    const REST_API_KEY = '9e7786142c02ceecc7551f66b0d94383'; // 카카오 내비 API 키
    const url = 'https://apis-navi.kakaomobility.com/v1/directions'; // 경로 요청 API 엔드포인트

    // 일정 섹션에서 관광지 ID 순서대로 가져오기
    const scheduleContainer = document.getElementById('schedule');
    const schedulePlaceIds = Array.from(scheduleContainer.children) // 일정 영역에 추가된 장소 ID를 가져옴
        .map(placeElement => placeElement.id.split('-')[1]); // place-1234 형식에서 숫자 ID 추출

    if (schedulePlaceIds.length < 2) { // 두 개 이상의 장소가 있어야 경로를 계산할 수 있음
        console.error("경로를 계산하려면 최소한 두 개의 장소가 필요합니다.");
        return;
    }

    const fullLinePath = []; // 전체 경로를 저장할 배열

    for (let i = 0; i < schedulePlaceIds.length - 1; i++) {
        const placeId1 = schedulePlaceIds[i];
        const placeId2 = schedulePlaceIds[i + 1];

        // 출발지와 도착지 좌표를 경로 배열에서 가져옴
        const origin = `${scheduleRoutes[placeId1].getLng()},${scheduleRoutes[placeId1].getLat()}`;
        const destination = `${scheduleRoutes[placeId2].getLng()},${scheduleRoutes[placeId2].getLat()}`;

        const queryParams = new URLSearchParams({
            origin: origin,
            destination: destination
        });

        const headers = {
            Authorization: `KakaoAK ${REST_API_KEY}`, // API 키를 헤더에 포함
            'Content-Type': 'application/json'
        };

        const requestUrl = `${url}?${queryParams}`; // 완성된 URL

        try {
            const response = await fetch(requestUrl, { method: 'GET', headers: headers });

            if (!response.ok) {
                throw new Error(`HTTP 오류! 상태 코드: ${response.status}`);
            }

            const data = await response.json();

            // 경로의 좌표 데이터를 가져옴
            data.routes[0].sections[0].roads.forEach(road => {
                road.vertexes.forEach((vertex, index) => {
                    if (index % 2 === 0) { // 좌표가 (위도, 경도) 쌍으로 구성되어 있어 홀수 번째 좌표는 생략
                        fullLinePath.push(new kakao.maps.LatLng(road.vertexes[index + 1], road.vertexes[index]));
                    }
                });
            });
        } catch (error) {
            console.error('오류 발생:', error); // 오류 발생 시 로그
        }
    }

    // 이전 경로 지우기
    if (currentPathPolyline) {
        currentPathPolyline.setMap(null); // 지도에서 폴리라인 제거
    }

    // 새로운 경로 그리기
    currentPathPolyline = new kakao.maps.Polyline({
        path: fullLinePath, // 경로 설정
        strokeWeight: 5, // 선 두께
        strokeColor: '#FF0000', // 선 색깔
        strokeOpacity: 0.7, // 선 투명도
        strokeStyle: 'solid' // 선 스타일
    });

    currentPathPolyline.setMap(map); // 지도에 경로 표시
}

// 경로 지우기 기능
function clearRoute() {
    if (currentPathPolyline) {
        currentPathPolyline.setMap(null); // 지도에서 경로 제거
    }
}

// 페이지 로드 시 저장된 관광지 목록 불러오기 및 모달 설정
document.addEventListener('DOMContentLoaded', function () {
    loadSavedPlaces(); // 저장된 장소 로드

    // 모달 관련 요소 선택
    modal = document.querySelector('.modal');
    modalBackdrop = document.querySelector('.modal-backdrop');
    modalClose = document.querySelector('.close_btn');

    // 모달 닫기 버튼 클릭 시 모달 닫기
    modalClose.addEventListener('click', function() {
        modal.classList.remove('on');
        modalBackdrop.classList.remove('on');
    });

    // 모달 외부 클릭 시 모달 닫기
    modalBackdrop.addEventListener('click', function() {
        modal.classList.remove('on');
        modalBackdrop.classList.remove('on');
    });
});

// 관광지 상세정보 모달 열기
function openTouristModal(contentId, contentTypeId) {
    fetch(`/touristSpot/Json/tourist-information?contentId=${contentId}&contentTypeId=${contentTypeId}`) // 관광지 상세 정보 요청
        .then(response => response.json())
        .then(data => {
            const touristDetail = data.touristDetail;
            const accessibilityInfo = data.accessibilityInfo;

            // 모달의 제목, 이미지, 설명 설정
            document.getElementById('modal-title').innerText = touristDetail.title || "제목 없음";
            document.getElementById('modal-image').src = touristDetail.firstimage || '/images/placeholder.jpg';
            document.getElementById('modal-overview').innerText = touristDetail.overview || "설명 정보 없음";

            // 오른쪽 정보 설정
            document.getElementById('modal-address').innerText = touristDetail.addr1 || "주소 정보 없음";
            document.getElementById('modal-tel').innerText = touristDetail.tel || "전화번호 정보 없음";
            document.getElementById('modal-homepage').innerHTML = touristDetail.homepage || "홈페이지 정보 없음";
            document.getElementById('modal-parking').innerText = accessibilityInfo.parking || "주차 정보 없음";
            document.getElementById('modal-restroom').innerText = accessibilityInfo.restroom || "장애인 화장실 정보 없음";
            document.getElementById('modal-wheelchair').innerText = accessibilityInfo.wheelchair || "휠체어 정보 없음";
            document.getElementById('modal-stroller').innerText = accessibilityInfo.stroller || "유모차 정보 없음";
            document.getElementById('modal-blindhandicapetc').innerText = accessibilityInfo.blindhandicapetc || "시각장애인 편의 정보 없음";
            document.getElementById('modal-elevator').innerText = accessibilityInfo.elevator || "엘리베이터 정보 없음";

            // 모달 및 배경 활성화
            modal.classList.add('on');
            modalBackdrop.classList.add('on');
        })
        .catch(error => console.error('Error fetching tourist detail:', error));
}
