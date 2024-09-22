// 지도 및 관련 변수 초기화
let map;  // 전역 변수로 선언
let scheduleRoutes = {};  // 일정 경로 저장 객체
let markers = {};  // 마커 저장 객체
let currentPathPolyline = null;  // 경로 폴리라인 저장 객체
let favoritePlaces = {};  // 저장된 관심 관광지 정보 저장 객체
let selectedTouristSpots = [];  // 전역으로 관광지 정보 저장 배열

document.addEventListener('DOMContentLoaded', function () {
    const pages = [
        document.getElementById('mainPage'),
        document.getElementById('durationPage'),
        document.getElementById('regionPage'),
        document.getElementById('barrierPage'),
        document.getElementById('selectPage'),
        document.getElementById('planPage'),
        document.getElementById('completionPage')
    ];

    let currentPageIndex = 0;

    // 선택된 항목들을 저장할 객체
    let travelPlan = {
        duration: '',
        region: '',
        barriers: [],
        selectedPlaces: [],
        tripName: ''
    };
    const userId = "사용자"; // 예시로 사용자 ID를 "사용자"로 설정, 실제로는 로그인 정보를 사용

    // 권역별 도시 목록
    const regionCities = {
        수도권: ["서울특별시", "인천광역시", "수원시", "파주시", "용인시"],
        강원권: ["속초시", "강릉시", "춘천시", "원주시", "평창군"],
        충청권: ["대전광역시", "청주시", "천안시"],
        전라권: ["광주광역시", "여수시", "전주시", "순천시", "군산시"],
        경상권: ["대구광역시", "부산광역시", "울산광역시", "경주시", "포항시", "거제시", "창원시"],
        제주권: ["제주시", "서귀포시"]
    };

    // 페이지 초기화 - 첫 번째 페이지만 보이게 함
    function showPage(index) {
        pages.forEach((page, i) => {
            page.style.display = i === index ? 'block' : 'none';
        });
    }

    // 처음에는 메인 페이지만 보이도록 설정
    showPage(currentPageIndex);

    // next-btn 클릭 시 다음 페이지로 이동
    document.querySelectorAll('.next-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            // selectedRegion이 null일 경우 페이지 전환을 막음
            if (!selectedRegion && currentPageIndex === 1) {
                alert("권역을 선택해주세요.");
                return; // 권역이 선택되지 않으면 페이지 전환하지 않음
            }

            if (currentPageIndex < pages.length - 1) {
                currentPageIndex++;
                showPage(currentPageIndex);
                if (currentPageIndex === 2) {
                    // 2번 지역 선택 페이지에서 버튼 업데이트
                    updateRegionButtons(selectedRegion);
                }
                if (currentPageIndex === 4) {
                    // 4번 페이지: 관광지 리스트 생성
                    generateTouristSpots();
                }
                if (currentPageIndex === 5) {
                    // 5번 페이지: 선택한 관광지 정렬하기
                    generateSortableList();
                    initializeDragEvents(); // 드래그 기능 활성화
                }
                if (currentPageIndex === 6) {
                    loadSelectedTouristSpots(); // 여행 완료 페이지에 도착하면 로컬 스토리지에서 값 불러오기
                    finalizeTripName();
                }
            }
        });
    });

    // 이전 버튼 클릭 시 이전 페이지로 이동
    document.querySelector('.back-button').addEventListener('click', function () {
        if (currentPageIndex > 0) {
            currentPageIndex--;
            showPage(currentPageIndex);
        }
    });

    // 권역 선택 관련 스크립트 추가
    let selectedRegion = null;

    // 모든 권역 동그라미 요소 선택
    const regionCircles = document.querySelectorAll('.circle');

    // '다음' 버튼 선택
    const nextBtn = document.querySelector('.next-btn');

    // 각 권역 클릭 시 동작 설정
    regionCircles.forEach(circle => {
        circle.addEventListener('click', function () {
            // 선택된 권역 저장
            selectedRegion = this.getAttribute('data-region');

            // 모든 동그라미의 스타일을 초기화
            regionCircles.forEach(c => {
                c.style.backgroundColor = 'rgba(0, 123, 255, 0.5)';
            });

            // 클릭된 동그라미의 스타일 변경
            this.style.backgroundColor = 'rgba(40, 167, 69, 0.7)'; // 선택된 권역은 초록색으로

            // 다음 버튼 활성화
            nextBtn.disabled = false;
            nextBtn.classList.add('active');
        });
    });

    // 지역 선택 페이지의 버튼을 업데이트하는 함수
    function updateRegionButtons(region) {
        const regionButtonsContainer = document.querySelector('.region-buttons');
        regionButtonsContainer.innerHTML = '';  // 기존 버튼 초기화

        const cities = regionCities[region];  // 선택된 권역에 맞는 도시 목록 가져오기
        cities.forEach(city => {
            const button = document.createElement('button');
            button.classList.add('region-btn');
            button.textContent = city;

            // 버튼 클릭 이벤트 추가
            button.addEventListener('click', function () {
                travelPlan.region = city;
                document.querySelectorAll('.region-btn').forEach(btn => {
                    btn.style.backgroundColor = '#f9f9f9';
                    btn.style.color = '#555';
                });
                this.style.backgroundColor = '#89B873';
                this.style.color = '#ffffff';
            });

            regionButtonsContainer.appendChild(button);
        });
    }

    // 무장애 시설 선택 버튼 클릭 시 이벤트 처리
    const barrierButtons = document.querySelectorAll('.barrier-btn');
    barrierButtons.forEach(button => {
        button.addEventListener('click', function () {
            const selectedBarrier = this.value;
            const index = travelPlan.barriers.indexOf(selectedBarrier);
            if (index === -1) {
                travelPlan.barriers.push(selectedBarrier);
                this.style.backgroundColor = '#89B873';  // 선택된 상태
            } else {
                travelPlan.barriers.splice(index, 1);
                this.style.backgroundColor = '#f9f9f9';  // 선택 해제 상태
            }
        });
    });

    // 관광지 생성 함수
    function generateTouristSpots() {
        const region = travelPlan.region;  // 선택한 지역
        const accessibleFeature = travelPlan.barriers[0];  // 첫 번째 선택된 무장애 정보 (여러 개 가능 시 배열로 처리)

        // API 호출
        fetch(`http://localhost:8080/touristSpot/Json/accessible-tourist-spots?region=${region}&accessibleFeature=${accessibleFeature}`)
            .then(response => response.json())
            .then(data => {
                const selectBox = document.querySelector('.select-box');
                selectBox.innerHTML = '';  // 기존 항목 초기화

                // 받아온 관광지 데이터로 목록 생성
                data.forEach(spot => {
                    const spotElement = document.createElement('div');
                    spotElement.classList.add('spot-item');
                    spotElement.innerHTML = `
                    <img src="${spot.firstimage}" alt="관광지 이미지">
                    <h3>${spot.title}</h3>
                    <p>${spot.addr1}</p>
                `;

                    // 클릭 이벤트: 선택/해제
                    spotElement.addEventListener('click', function () {
                        if (travelPlan.selectedPlaces.includes(spot)) {
                            travelPlan.selectedPlaces = travelPlan.selectedPlaces.filter(item => item !== spot);
                            this.style.backgroundColor = '#f9f9f9';  // 선택 해제
                        } else {
                            travelPlan.selectedPlaces.push(spot);  // spot 객체 전체를 추가
                            this.style.backgroundColor = '#89B873';  // 선택됨
                        }
                    });

                    selectBox.appendChild(spotElement);
                });
            })
            .catch(error => console.error('관광지 정보를 불러오는 중 오류 발생:', error));
    }

    // 5번 페이지에서 선택한 관광지 목록을 드래그 가능한 리스트로 표시
    function generateSortableList() {
        const sortableList = document.getElementById('sortable');
        sortableList.innerHTML = ''; // 기존 항목 초기화
        travelPlan.selectedPlaces.forEach((place, index) => {
            const listItem = document.createElement('li');
            listItem.classList.add('list-item');
            listItem.setAttribute('data-content-id', place.contentid);  // contentId 저장
            listItem.setAttribute('data-content-type-id', place.contenttypeid);  // contentTypeId 저장
            listItem.setAttribute('data-mapx', place.mapx);  // 경도 저장
            listItem.setAttribute('data-mapy', place.mapy);  // 위도 저장
            listItem.setAttribute('data-title', place.title);  // title 저장
            listItem.innerHTML = `
        <div class="content">
            <div class="left-info">
                <strong>${place.title}</strong>
                <p>${place.addr1}</p>
            </div>
        </div>
        `;

            sortableList.appendChild(listItem);
        });

        console.log("Sortable list items:", document.querySelectorAll('.list-item'));  // 항목들이 제대로 추가되었는지 확인

        initializeDragEvents(); // 드래그 앤 드롭 기능 활성화
    }

    // 드래그 앤 드롭 기능 활성화
    function initializeDragEvents() {
        let draggedItem = null;

        document.querySelectorAll('.list-item').forEach(item => {
            const dragHandle = item.querySelector('.drag-handle');

            dragHandle.addEventListener('mousedown', function (event) {
                draggedItem = item;
                item.classList.add('dragging');

                document.addEventListener('mousemove', onMouseMove);
                document.addEventListener('mouseup', onMouseUp);
            });

            function onMouseMove(event) {
                if (!draggedItem) return;

                draggedItem.style.position = 'absolute';
                draggedItem.style.top = `${event.clientY - draggedItem.offsetHeight / 2}px`;
                draggedItem.style.left = `${event.clientX - draggedItem.offsetWidth / 2}px`;
            }

            function onMouseUp() {
                if (!draggedItem) return;

                draggedItem.classList.remove('dragging');
                draggedItem.style.position = '';
                draggedItem.style.top = '';
                draggedItem.style.left = '';
                draggedItem = null;

                document.removeEventListener('mousemove', onMouseMove);
                document.removeEventListener('mouseup', onMouseUp);
            }
        });
    }

    // 6번 페이지: 여행 이름 설정 및 여행 완료 처리
    function finalizeTripName() {
        const tripNameInput = document.getElementById('tripName').value;
        if (tripNameInput.trim() === "") {
            travelPlan.tripName = `${userId}의 멋진 여행`; // 기본 여행 이름 설정
        } else {
            travelPlan.tripName = tripNameInput;
        }

        // 여행 이름과 정리된 여행 정보를 출력하거나 저장
        console.log('최종 여행 계획:', travelPlan);
    }
});

document.querySelector('#planPage .next-btn').addEventListener('click', function() {
    const tripName = document.getElementById('tripName').value;
    const sortableListItems = document.querySelectorAll('#sortable .list-item');
    selectedTouristSpots = [];

    sortableListItems.forEach(item => {
        const { contentId, contentTypeId, mapx, mapy,title  } = item.dataset;

        if (contentId && contentTypeId && mapx && mapy&& title) {
            selectedTouristSpots.push({
                contentId: contentId,
                contentTypeId: contentTypeId,
                mapx: parseFloat(mapx),
                mapy: parseFloat(mapy),
                title: title  // title 추가
            });
        } else {
            console.error('Missing attributes in list item:', { contentId, contentTypeId, mapx, mapy });
        }
    });

    console.log('Selected Tourist Spots after:', selectedTouristSpots);

    // 로컬 스토리지에 저장
    localStorage.setItem('selectedTouristSpots', JSON.stringify(selectedTouristSpots));

    const courseData = {
        courseName: tripName || "기본 여행 코스 이름",
        contentInfos: selectedTouristSpots
    };

    fetch('/courses/courses/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(courseData)
    })
        .then(response => {
            if (response.ok) {
                alert('여행 코스가 성공적으로 저장되었습니다!');
            } else {
                alert('코스 저장에 실패했습니다.');
            }
        })
        .catch(error => console.error('Error:', error));
});

// 자동차 경로 그리기 함수
async function drawCarRoute(touristSpots) {
    const REST_API_KEY = '9e7786142c02ceecc7551f66b0d94383';
    const url = 'https://apis-navi.kakaomobility.com/v1/directions';

    const fullLinePath = [];

    for (let i = 0; i < touristSpots.length - 1; i++) {
        const origin = `${touristSpots[i].mapx},${touristSpots[i].mapy}`;
        const destination = `${touristSpots[i + 1].mapx},${touristSpots[i + 1].mapy}`;

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
                throw new Error(`HTTP 오류! 상태 코드: ${response.status}`);
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
            console.error('오류 발생:', error);
        }
    }

    const polyline = new kakao.maps.Polyline({
        path: fullLinePath,
        strokeWeight: 5,
        strokeColor: '#00AFFF',
        strokeOpacity: 0.8,
        strokeStyle: 'solid'
    });

    polyline.setMap(map);
}

// 지도 생성 및 마커, 경로 표시 함수
function initMapAndMarkers(touristSpots) {
    const mapContainer = document.getElementById('kakao-map');
    const mapOption = {
        center: new kakao.maps.LatLng(touristSpots[0].mapy, touristSpots[0].mapx),
        level: 8
    };

    map = new kakao.maps.Map(mapContainer, mapOption);

    const linePath = [];

    touristSpots.forEach((spot, index) => {
        const markerPosition = new kakao.maps.LatLng(spot.mapy, spot.mapx);
        const marker = new kakao.maps.Marker({
            position: markerPosition,
            map: map
        });

        const infowindow = new kakao.maps.InfoWindow({
            content: `<div style="padding:5px;">${index + 1}. ${spot.title}</div>`
        });
        infowindow.open(map, marker);

        linePath.push(markerPosition);
    });

    /*const polyline = new kakao.maps.Polyline({
        path: linePath,
        strokeWeight: 5,
        strokeColor: '#FF0000',
        strokeOpacity: 0.8
    });
    polyline.setMap(map);*/
}

// 페이지가 로드되었을 때 실행되는 함수
document.addEventListener('DOMContentLoaded', function () {
    const completionPage = document.getElementById('completionPage');
    // 로컬 스토리지에서 값을 불러옴
    selectedTouristSpots = JSON.parse(localStorage.getItem('selectedTouristSpots')) || [];

    console.log('Starting the process of loading Kakao map...');
    console.log('Selected Tourist Spots:', selectedTouristSpots);

    const observer = new MutationObserver(function (mutations) {
        mutations.forEach(function (mutation) {
            console.log(mutation.target);
            if (mutation.target === completionPage && !completionPage.classList.contains('hidden')) {
                console.log('Completion page is visible');
                console.log(selectedTouristSpots);
                if (selectedTouristSpots.length > 0) {
                    initMapAndMarkers(selectedTouristSpots);
                    console.info('Kakao map initialized successfully.');
                    drawCarRoute(selectedTouristSpots);
                    setTimeout(() => {
                        map.relayout();
                        map.setCenter(new kakao.maps.LatLng(selectedTouristSpots[0].mapy, selectedTouristSpots[0].mapx));
                    }, 100);
                } else {
                    console.log('selected 오류');
                    alert('저장된 코스가 없습니다.');
                }
            }
        });
    });

    observer.observe(completionPage, { attributes: true, attributeFilter: ['class'] });
    // 완료 버튼 클릭 시 /users/mypage로 이동
    document.querySelector('#completionPage .complete-btn').addEventListener('click', function () {
        window.location.href = '/users/mypage';
    });
});

// selectedTouristSpots 배열을 로컬 스토리지에 저장
localStorage.setItem('selectedTouristSpots', JSON.stringify(selectedTouristSpots));

// 페이지 로드 시 로컬 스토리지에서 값 불러오기
document.addEventListener('DOMContentLoaded', function () {
    selectedTouristSpots = JSON.parse(localStorage.getItem('selectedTouristSpots')) || [];
    console.log('Loaded Selected Tourist Spots from localStorage:', selectedTouristSpots);
});

// 여행 완료 페이지에서 로컬 스토리지 값 불러오기
function loadSelectedTouristSpots() {
    // 로컬 스토리지에서 값을 불러옴
    selectedTouristSpots = JSON.parse(localStorage.getItem('selectedTouristSpots')) || [];
    console.log('Loaded Selected Tourist Spots from localStorage:', selectedTouristSpots);

    const completionPage = document.getElementById('completionPage');

    if (completionPage && selectedTouristSpots.length > 0) {
        initMapAndMarkers(selectedTouristSpots);
        drawCarRoute(selectedTouristSpots);
        console.info('Kakao map initialized successfully.');
    } else {
        console.log('Selected tourist spots not found or empty');
        alert('저장된 코스가 없습니다.');
    }
}