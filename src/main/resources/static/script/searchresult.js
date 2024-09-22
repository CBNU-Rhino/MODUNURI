document.getElementById('search-form').addEventListener('submit', function (e) {
    e.preventDefault(); // 폼 제출 기본 동작 막기
    const query = document.getElementById('search-input').value;

    // 검색어가 입력된 경우에만 API 호출
    if (query) {
        fetchTouristSpotData(query);
    }
});

function fetchTouristSpotData(query) {
    // 여기에 실제 API 엔드포인트 URL을 입력하세요
    const apiUrl = `https://api.example.com/tourist-spot?query=${encodeURIComponent(query)}`;

    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            displayTouristSpotData(data);
        })
        .catch(error => {
            console.error('Error fetching tourist spot data:', error);
        });
}

function displayTouristSpotData(data) {
    // 사진 렌더링
    const photoContainer = document.getElementById('photo-container');
    photoContainer.innerHTML = ''; // 기존 내용 지우기
    const photoElement = document.createElement('img');
    photoElement.src = data.photoUrl; // API에서 받은 사진 URL
    photoElement.alt = data.name; // 관광지 이름을 alt 속성으로 설정
    photoElement.className = 'tourist-photo';
    photoContainer.appendChild(photoElement);

    // 정보 렌더링
    const infoContainer = document.getElementById('info-container');
    infoContainer.innerHTML = ''; // 기존 내용 지우기
    const infoHtml = `
        <div class="info-block">
            <strong>관광지 이름:</strong> <span>${data.name}</span>
        </div>
        <div class="info-block">
            <strong>위치:</strong> <span>${data.location}</span>
        </div>
        <div class="info-block">
            <strong>특징:</strong> <span>${data.description}</span>
        </div>
    `;
    infoContainer.innerHTML = infoHtml;

    // 추가 정보 렌더링
    const detailedInfo = document.getElementById('detailed-info');
    detailedInfo.innerText = data.detailedInfo || "추가 정보가 없습니다.";
}