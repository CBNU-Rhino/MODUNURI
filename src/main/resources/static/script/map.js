// 지도 및 관련 변수 초기화
var mapContainer = document.getElementById('map'),
    mapOption = {
        center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울 시청의 위도와 경도
        level: 3 // 지도의 확대 레벨
    };

var map = new kakao.maps.Map(mapContainer, mapOption); // 지도 생성
var scheduleRoutes = []; // 일정 경로 저장 배열
var markers = []; // 마커 저장 배열
var currentPathPolyline = null; // 경로 폴리라인 저장 객체
var favoritePlaces = {}; // 저장된 관심 관광지 정보 저장 객체

// 저장된 관광지 목록 불러오기
function loadSavedPlaces() {
    fetch('/users/getFavoriteContents')
        .then(response => response.json())
        .then(favoritePlacesData => {
            favoritePlaces = favoritePlacesData;
            Object.keys(favoritePlaces).forEach(contentId => {
                const contentTypeId = favoritePlaces[contentId];
                fetch(`/touristSpot/Json/tourist-information?contentId=${contentId}&contentTypeId=${contentTypeId}`)
                    .then(response => response.json())
                    .then(data => {
                        displaySavedPlace(data.touristDetail);
                    })
                    .catch(error => console.error("Error fetching tourist information:", error));
            });
        })
        .catch(error => console.error('Error loading saved places:', error));
}

// 저장된 관광지 정보를 박스로 표시
function displaySavedPlace(touristDetail) {
    const savedPlaces = document.querySelector('#saved-places');
    const placeBox = document.createElement('div');
    placeBox.classList.add('place-box');
    placeBox.setAttribute('draggable', 'true');
    placeBox.setAttribute('ondragstart', 'drag(event)');
    placeBox.id = `place-${touristDetail.contentid}`;

    const imageUrl = touristDetail.firstimage || '/images/placeholder.png';
    const title = touristDetail.title || "제목 없음";
    const address = touristDetail.addr1 || "주소 없음";

    placeBox.innerHTML = `
        <div class="place-info">
            <img src="${imageUrl}" alt="${title}" class="place-image" />
            <div class="place-text">
                <h4>${title}</h4>
                <p>${address}</p>
            </div>
        </div>
    `;

    savedPlaces.appendChild(placeBox);
}

// 마커 추가 및 경로 그리기
function addMarkerAndRoute(touristDetail) {
    const coords = new kakao.maps.LatLng(touristDetail.mapy, touristDetail.mapx);

    // 마커 생성
    const marker = new kakao.maps.Marker({
        map: map,
        position: coords
    });

    // 인포윈도우 생성
    var infowindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:5px;">${touristDetail.title}</div>` // 관광지 이름 표시
    });

    infowindow.open(map, marker); // 마커에 인포윈도우 표시

    // 경로와 마커 추가
    scheduleRoutes.push(coords);
    markers.push(marker);
}

// 자동차 경로를 가져오는 함수 (카카오 내비 API 사용)
async function getCarDirection() {
    const REST_API_KEY = '9e7786142c02ceecc7551f66b0d94383';
    const url = 'https://apis-navi.kakaomobility.com/v1/directions';

    if (scheduleRoutes.length < 2) {
        console.error("At least two places are required to calculate the route.");
        return;
    }

    // 출발지와 목적지 설정
    const origin = `${scheduleRoutes[0].getLng()},${scheduleRoutes[0].getLat()}`;
    const destination = `${scheduleRoutes[scheduleRoutes.length - 1].getLng()},${scheduleRoutes[scheduleRoutes.length - 1].getLat()}`;

    const queryParams = new URLSearchParams({
        origin: origin,
        destination: destination
    });

    const headers = {
        Authorization: `KakaoAK ${REST_API_KEY}`,
        'Content-Type': 'application/json'
    };

    const requestUrl = `${url}?${queryParams}`;

    try {
        const response = await fetch(requestUrl, {
            method: 'GET',
            headers: headers
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();
        const linePath = [];

        data.routes[0].sections[0].roads.forEach(road => {
            road.vertexes.forEach((vertex, index) => {
                if (index % 2 === 0) {
                    linePath.push(new kakao.maps.LatLng(road.vertexes[index + 1], road.vertexes[index]));
                }
            });
        });

        if (currentPathPolyline) {
            currentPathPolyline.setMap(null); // 기존 경로 제거
        }

        currentPathPolyline = new kakao.maps.Polyline({
            path: linePath,
            strokeWeight: 5,
            strokeColor: '#FF0000',
            strokeOpacity: 0.7,
            strokeStyle: 'solid'
        });

        currentPathPolyline.setMap(map); // 경로 지도에 표시
    } catch (error) {
        console.error('Error:', error);
    }
}

// 드래그 앤 드롭 기능
function allowDrop(ev) {
    ev.preventDefault(); // 기본 동작을 막아 드롭 가능하도록 설정
}

function drag(ev) {
    ev.dataTransfer.setData("text", ev.target.id); // 드래그된 요소의 ID 저장
}

function drop(ev) {
    ev.preventDefault(); // 기본 동작을 막음
    const data = ev.dataTransfer.getData("text"); // 드래그된 요소의 ID 가져옴
    console.log(`Dropped data: ${data}`); // 디버그용 로그 추가

    const targetContainer = ev.target;

    // 드롭할 수 있는 영역인지 확인
    if (targetContainer.classList.contains('drop-area')) {
        const placeElement = document.getElementById(data);
        if (placeElement) {
            console.log("Element found:", placeElement); // 디버그용 로그
            targetContainer.appendChild(placeElement); // 드래그된 요소를 드롭된 영역에 추가

            const placeId = data.split('-')[1]; // place-1234 형식에서 id만 추출

            fetch(`/touristSpot/Json/tourist-information?contentId=${placeId}&contentTypeId=${favoritePlaces[placeId]}`)
                .then(response => response.json())
                .then(data => {
                    addMarkerAndRoute(data.touristDetail); // 마커 및 경로 추가
                })
                .catch(error => console.error("Error fetching tourist information:", error));
        } else {
            console.error("Invalid element ID:", data); // 요소가 없을 때의 오류 처리
        }
    }
}

// 경로 지우기 기능
// 경로 지우기 기능
function clearRoute() {
    if (currentPathPolyline) {
        currentPathPolyline.setMap(null); // 지도에서 경로 제거
    }
    // 경로만 지우고, 마커와 좌표는 남겨둡니다.
}

// 페이지 로드 시 저장된 관광지 목록 불러오기
document.addEventListener('DOMContentLoaded', function () {
    loadSavedPlaces(); // 저장된 장소 로드

    // 드래그 가능한 요소에 드래그 이벤트 추가
    document.querySelectorAll('.place-box').forEach(placeBox => {
        placeBox.addEventListener('dragstart', drag);
    });

    // 드롭 가능 영역에 드롭 이벤트 추가
    document.querySelectorAll('.drop-area').forEach(dropArea => {
        dropArea.addEventListener('dragover', allowDrop);
        dropArea.addEventListener('drop', drop);
    });
});

// 모달 닫기 기능
function closeModal() {
    document.getElementById('search-result-modal').style.display = "none";
}
