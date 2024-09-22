// 모달 관련 변수 전역 선언
let modal, modalBackdrop, modalClose;

// 지도 및 관련 변수 초기화
var mapContainer = document.getElementById('map'),
    mapOption = {
        center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울 시청의 위도와 경도
        level: 8 // 지도의 확대 레벨
    };

var map = new kakao.maps.Map(mapContainer, mapOption); // 지도 생성
var scheduleRoutes = {}; // 일정 경로 저장 객체 (ID로 관리)
var markers = {}; // 마커 저장 객체 (ID로 관리)
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
                <button onclick="openTouristModal('${touristDetail.contentid}', '${touristDetail.contenttypeid}')">상세정보</button>
            </div>
        </div>
    `;

    savedPlaces.appendChild(placeBox);
}

// 마커 추가 및 경로 그리기
function addMarkerAndRoute(touristDetail) {
    const coords = new kakao.maps.LatLng(touristDetail.mapy, touristDetail.mapx);
    const placeId = touristDetail.contentid; // 관광지 ID

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

    // 경로와 마커 추가 (ID로 관리)
    scheduleRoutes[placeId] = coords;
    markers[placeId] = marker;
}

// 마커 제거 함수
function removeMarker(placeId) {
    if (markers[placeId]) {
        markers[placeId].setMap(null); // 지도에서 마커 제거
        delete markers[placeId]; // 마커 객체에서 삭제
        delete scheduleRoutes[placeId]; // 경로 배열에서도 삭제
    }
}

// 자동차 경로를 가져오는 함수 (카카오 내비 API 사용)
async function getCarDirection() {
    const REST_API_KEY = '9e7786142c02ceecc7551f66b0d94383';
    const url = 'https://apis-navi.kakaomobility.com/v1/directions';

    const placeIds = Object.keys(scheduleRoutes);
    if (placeIds.length < 2) {
        console.error("At least two places are required to calculate the route.");
        return;
    }

    const fullLinePath = [];

    for (let i = 0; i < placeIds.length - 1; i++) {
        const origin = `${scheduleRoutes[placeIds[i]].getLng()},${scheduleRoutes[placeIds[i]].getLat()}`;
        const destination = `${scheduleRoutes[placeIds[i + 1]].getLng()},${scheduleRoutes[placeIds[i + 1]].getLat()}`;

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

            data.routes[0].sections[0].roads.forEach(road => {
                road.vertexes.forEach((vertex, index) => {
                    if (index % 2 === 0) {
                        fullLinePath.push(new kakao.maps.LatLng(road.vertexes[index + 1], road.vertexes[index]));
                    }
                });
            });
        } catch (error) {
            console.error('Error:', error);
        }
    }

    if (currentPathPolyline) {
        currentPathPolyline.setMap(null);
    }

    currentPathPolyline = new kakao.maps.Polyline({
        path: fullLinePath,
        strokeWeight: 5,
        strokeColor: '#FF0000',
        strokeOpacity: 0.7,
        strokeStyle: 'solid'
    });

    currentPathPolyline.setMap(map); // 경로 지도에 표시
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
    const targetContainer = ev.target;
    const placeId = data.split('-')[1]; // place-1234 형식에서 id만 추출

    if (targetContainer.classList.contains('drop-area')) {
        const placeElement = document.getElementById(data);
        if (placeElement) {
            targetContainer.appendChild(placeElement); // 드래그된 요소를 드롭된 영역에 추가

            if (targetContainer.id === 'saved-places') {
                removeMarker(placeId);
            } else {
                fetch(`/touristSpot/Json/tourist-information?contentId=${placeId}&contentTypeId=${favoritePlaces[placeId]}`)
                    .then(response => response.json())
                    .then(data => {
                        addMarkerAndRoute(data.touristDetail); // 마커 및 경로 추가
                        // 지도 중심을 해당 관광지의 좌표로 이동
                        const coords = new kakao.maps.LatLng(data.touristDetail.mapy, data.touristDetail.mapx);
                        map.setCenter(coords); // 지도 중심 이동
                    })
                    .catch(error => console.error("Error fetching tourist information:", error));
            }
        } else {
            console.error("Invalid element ID:", data); // 요소가 없을 때의 오류 처리
        }
    }
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

    // 드래그 가능한 요소에 드래그 이벤트 추가
    document.querySelectorAll('.place-box').forEach(placeBox => {
        placeBox.addEventListener('dragstart', onDragStart); // 드래그 시작 시
        placeBox.addEventListener('dragover', onDragOver);   // 드래그가 진행될 때
        placeBox.addEventListener('dragenter', onDragEnter); // 드래그가 다른 요소 위로 들어갈 때
        placeBox.addEventListener('drop', onDrop);
    });

    // 드롭 가능 영역에 드롭 이벤트 추가
    document.querySelectorAll('.drop-area').forEach(dropArea => {
        dropArea.addEventListener('dragover', allowDrop);
        dropArea.addEventListener('drop', drop);
    });

    // 모달 관련 요소 선택
    modal = document.querySelector('.modal');
    modalBackdrop = document.querySelector('.modal-backdrop');
    modalClose = document.querySelector('.close_btn');

    // 모달 닫기 버튼 클릭 시
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

// 상세정보 모달 표시 함수
function openTouristModal(contentId, contentTypeId) {
    fetch(`/touristSpot/Json/tourist-information?contentId=${contentId}&contentTypeId=${contentTypeId}`)
        .then(response => response.json())
        .then(data => {
            const touristDetail = data.touristDetail;
            const accessibilityInfo = data.accessibilityInfo;

            // 관광지 정보 설정
            document.getElementById('modal-title').innerText = touristDetail.title || "제목 없음";
            document.getElementById('modal-image').src = touristDetail.firstimage || '/images/placeholder.png';
            document.getElementById('modal-overview').innerText = touristDetail.overview || "정보 없음";
            document.getElementById('modal-address').innerText = touristDetail.addr1 || "주소 없음";
            document.getElementById('modal-tel').innerText = touristDetail.tel || "전화번호 정보 없음";

            // 추가된 정보 설정 (homepage, parking, restroom, wheelchair, stroller, blindhandicapetc, elevator)
            document.getElementById('modal-homepage').innerHTML = touristDetail.homepage || "홈페이지 정보 없음";
            document.getElementById('modal-parking').innerText = accessibilityInfo.parking || "주차 정보 없음";
            document.getElementById('modal-restroom').innerText = accessibilityInfo.restroom || "장애인 화장실 정보 없음";
            document.getElementById('modal-wheelchair').innerText = accessibilityInfo.wheelchair || "휠체어 정보 없음";
            document.getElementById('modal-stroller').innerText = accessibilityInfo.stroller || "유모차 정보 없음";
            document.getElementById('modal-blindhandicapetc').innerText = accessibilityInfo.blindhandicapetc || "시각장애인 편의 정보 없음";
            document.getElementById('modal-elevator').innerText = accessibilityInfo.elevator || "엘리베이터 정보 없음";

            // 모달 및 배경 열기
            modal.classList.add('on');
            modalBackdrop.classList.add('on');
        })
        .catch(error => console.error('Error fetching tourist detail:', error));
}


// 드래그 시작 시 선택된 요소 저장
let selectedElement = null;
let selectedElementIndex = null;

function onDragStart(event) {
    selectedElement = event.target; // 드래그된 요소 저장
    selectedElementIndex = [...selectedElement.parentNode.children].indexOf(selectedElement); // 선택된 요소의 인덱스를 저장
    selectedElement.classList.add('dragging'); // 드래그 시 스타일 변경
}

function onDragOver(event) {
    event.preventDefault(); // 드롭을 허용하기 위해 기본 동작을 막음
}

function onDrop(event) {
    event.preventDefault(); // 기본 동작 막기

    const targetElement = event.target.closest('.place-box');
    if (!targetElement || targetElement === selectedElement) return; // 유효하지 않은 타겟이면 무시

    const targetElementIndex = [...targetElement.parentNode.children].indexOf(targetElement);
    const selectedElementIndex = [...selectedElement.parentNode.children].indexOf(selectedElement);

    // 애니메이션을 위해 먼저 위치 변경
    if (targetElementIndex > selectedElementIndex) {
        // 아래로 이동할 경우, targetElement 이후로 배치
        targetElement.after(selectedElement);
        moveElement(targetElement, selectedElement, "down");
        // 위로 이동할 경우, targetElement 이전으로 배치
    } else {
        targetElement.before(selectedElement);
        moveElement(targetElement, selectedElement, "up");
    }

    // 애니메이션 리셋 후 초기화
    requestAnimationFrame(() => {
        selectedElement.classList.remove('dragging');
    });
}

function moveElement(targetElement, selectedElement, direction) {
    const targetElementTop = targetElement.getBoundingClientRect().top;
    const selectedElementTop = selectedElement.getBoundingClientRect().top;
    const distance = targetElementTop - selectedElementTop;
    if (direction === "down") {
        selectedElement.style.transform = `translateY(${distance}px)`;
        targetElement.style.transform = `translateY(${-distance}px)`;
    } else {
        selectedElement.style.transform = `translateY(${distance}px)`;
        targetElement.style.transform = `translateY(${-distance}px)`;
    }
    // 일정 시간이 지나면 위치 고정
    setTimeout(() => {
        selectedElement.style.transform = '';
        targetElement.style.transform = '';
    }, 300);
}

function changePosition(targetElement) {
    if (!targetElement) {
        console.error("targetElement is not defined.");
        return;
    }

    const targetElementTop = targetElement.getBoundingClientRect().top;
    const selectedElementTop = selectedElement.getBoundingClientRect().top;

    const distance = targetElementTop - selectedElementTop;

    // 애니메이션을 통해 두 요소가 움직이는 것을 시각적으로 표현
    selectedElement.style.transform = `translateY(${distance}px)`;
    targetElement.style.transform = `translateY(${-distance}px)`;

    console.log(`Animating: ${selectedElement.id} moving ${distance}px`); // 로그 추가

    // 애니메이션 적용
    addAnimation(selectedElement);
    addAnimation(targetElement);

    // 실제 위치 변경은 setTimeout으로 애니메이션이 완료된 후 수행
    setTimeout(() => {
        if (distance > 0) {
            targetElement.after(selectedElement);
            console.log(`Changed position: ${selectedElement.id} after ${targetElement.id}`); // 로그 추가
        } else {
            targetElement.before(selectedElement);
            console.log(`Changed position: ${selectedElement.id} before ${targetElement.id}`); // 로그 추가
        }

        // 위치 변경 후 transform 속성을 초기화
        selectedElement.style.transform = '';
        targetElement.style.transform = '';
    }, 300); // 애니메이션 지속 시간
}

function addAnimation(element) {
    element.classList.add('animation'); // 애니메이션 적용
    console.log(`Animation applied to: ${element.id}`); // 로그 추가
}


