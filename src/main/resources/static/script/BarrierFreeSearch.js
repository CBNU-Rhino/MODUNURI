const sidoOptions = {
    '서울특별시': ['종로구', '중구', '용산구', '성동구', '광진구', '동대문구', '중랑구', '성북구', '강북구', '도봉구', '노원구', '은평구', '서대문구', '마포구', '양천구', '강서구', '구로구', '금천구', '영등포구', '동작구', '관악구', '서초구', '강남구', '송파구', '강동구'],
    '부산광역시': ['중구', '서구', '동구', '영도구', '부산진구', '동래구', '남구', '북구', '해운대구', '사하구', '금정구', '강서구', '연제구', '수영구', '사상구', '기장군'],
    '대구광역시': ['중구', '동구', '서구', '남구', '북구', '수성구', '달서구', '달성군'],
    '인천광역시': ['중구', '동구', '미추홀구', '연수구', '남동구', '부평구', '계양구', '서구', '강화군', '옹진군'],
    '광주광역시': ['동구', '서구', '남구', '북구', '광산구'],
    '대전광역시': ['동구', '중구', '서구', '유성구', '대덕구'],
    '울산광역시': ['중구', '남구', '동구', '북구', '울주군'],
    '세종특별자치시': ['세종특별자치시'],
    '경기도': ['수원시', '성남시', '고양시', '용인시', '부천시', '안산시', '안양시', '남양주시', '화성시', '평택시', '의정부시', '시흥시', '파주시', '김포시', '광명시', '광주시', '군포시', '이천시', '양주시', '오산시', '구리시', '안성시', '포천시', '의왕시', '하남시', '여주시', '양평군', '동두천시', '과천시', '가평군', '연천군'],
    '강원도': ['춘천시', '원주시', '강릉시', '동해시', '태백시', '속초시', '삼척시', '홍천군', '횡성군', '영월군', '평창군', '정선군', '철원군', '화천군', '양구군', '인제군', '고성군', '양양군'],
    '충청북도': ['청주시', '충주시', '제천시', '보은군', '옥천군', '영동군', '증평군', '진천군', '괴산군', '음성군', '단양군'],
    '충청남도': ['천안시', '공주시', '보령시', '아산시', '서산시', '논산시', '계룡시', '당진시', '금산군', '부여군', '서천군', '청양군', '홍성군', '예산군', '태안군'],
    '전라북도': ['전주시', '군산시', '익산시', '정읍시', '남원시', '김제시', '완주군', '진안군', '무주군', '장수군', '임실군', '순창군', '고창군', '부안군'],
    '전라남도': ['목포시', '여수시', '순천시', '나주시', '광양시', '담양군', '곡성군', '구례군', '고흥군', '보성군', '화순군', '장흥군', '강진군', '해남군', '영암군', '무안군', '함평군', '영광군', '장성군', '완도군', '진도군', '신안군'],
    '경상북도': ['포항시', '경주시', '김천시', '안동시', '구미시', '영주시', '영천시', '상주시', '문경시', '경산시', '군위군', '의성군', '청송군', '영양군', '영덕군', '청도군', '고령군', '성주군', '칠곡군', '예천군', '봉화군', '울진군', '울릉군'],
    '경상남도': ['창원시', '진주시', '통영시', '사천시', '김해시', '밀양시', '거제시', '양산시', '의령군', '함안군', '창녕군', '고성군', '남해군', '하동군', '산청군', '함양군', '거창군', '합천군'],
    '제주특별자치도': ['제주시', '서귀포시']
};

let currentPage = 1;
const itemsPerPage = 12;
let totalPages = 1;
let currentData = [];  // 전체 데이터 저장용

function updateSido() {
    const regionSelect = document.getElementById('region');
    const sidoSelect = document.getElementById('sido');
    const selectedRegion = regionSelect.value;

    sidoSelect.innerHTML = '<option value="">시/구/군을 선택하세요</option>';

    if (selectedRegion) {
        const options = sidoOptions[selectedRegion];
        options.forEach(sido => {
            const option = document.createElement('option');
            option.value = sido;
            option.text = sido;
            sidoSelect.appendChild(option);
        });
    }
}

function getSelectedContentType() {
    const checkboxes = document.querySelectorAll('input[name="category"]:checked');
    const selectedTypes = Array.from(checkboxes).map(checkbox => checkbox.value);
    return selectedTypes.length > 0 ? selectedTypes.join(',') : null;
}

// 중복 항목 제거하는 함수
function removeDuplicates(data) {
    const uniqueItems = [];
    const seenContentIds = new Set();

    data.forEach(item => {
        if (!seenContentIds.has(item.contentid)) {
            uniqueItems.push(item);
            seenContentIds.add(item.contentid);
        }
    });

    return uniqueItems;
}

async function searchAccessibleItems() {
    const region = document.getElementById('region').value;
    const sigungu = document.getElementById('sido').value;
    const accessibleType = document.getElementById('accessibleType').value;
    const contentTypeId = document.querySelector('input[name="category"]:checked').value;
    const gallery = document.getElementById('gallery');
    gallery.innerHTML = '';  // 기존 검색 결과 초기화

    if (!contentTypeId) {
        alert("카테고리를 선택하세요.");
        return;
    }

    const apiUrl = `/api/tourist-accessible-info?region=${region}&sigungu=${sigungu}&contentTypeId=${contentTypeId}&accessibleType=${accessibleType}`;

    try {
        const response = await fetch(apiUrl);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        let data = await response.json();
        data = removeDuplicates(data);
        saveSearchState(region, sigungu, accessibleType, contentTypeId, data);

        // 전체 데이터를 currentData에 저장
        currentData = data;

        // 페이지 수 계산
        totalPages = Math.ceil(currentData.length / itemsPerPage); // 총 페이지 수 계산

        // 검색 결과를 표시
        displayGalleryPage(currentPage); // 첫 번째 페이지 표시
        updatePagination(); // 페이지네이션 업데이트

        // 검색 결과가 없을 경우 처리
        if (data.length === 0) {
            gallery.innerHTML = '<p>검색 결과가 없습니다.</p>';
        }

    } catch (error) {
        console.error('Error fetching tourist data:', error);
        gallery.innerHTML = `<p>데이터를 불러오는 중 오류가 발생했습니다. 오류 메시지: ${error.message}</p>`;
    }
}


function updatePagination() {
    const pagination = document.querySelector('.pagination');
    pagination.innerHTML = '';

    const prevArrow = document.createElement('div');
    prevArrow.classList.add('page-arrow');
    prevArrow.innerHTML = `<`;
    prevArrow.addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            displayGalleryPage(currentPage);
            updatePagination();
        }
    });
    pagination.appendChild(prevArrow);

    for (let i = 1; i <= totalPages; i++) {
        const pageNumber = document.createElement('div');
        pageNumber.classList.add('page-number');
        pageNumber.textContent = i;
        if (i === currentPage) {
            pageNumber.classList.add('active');
        }
        pageNumber.addEventListener('click', () => {
            currentPage = i;
            displayGalleryPage(currentPage);
            updatePagination();
        });
        pagination.appendChild(pageNumber);
    }

    const nextArrow = document.createElement('div');
    nextArrow.classList.add('page-arrow');
    nextArrow.innerHTML = `>`;
    nextArrow.addEventListener('click', () => {
        if (currentPage < totalPages) {
            currentPage++;
            displayGalleryPage(currentPage);
            updatePagination();
        }
    });
    pagination.appendChild(nextArrow);
}

function displayGalleryPage(page) {
    const gallery = document.getElementById('gallery');
    gallery.innerHTML = '';  // 기존 갤러리 초기화

    const start = (page - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const itemsToShow = currentData.slice(start, end);

    itemsToShow.forEach(item => {
        const galleryItem = document.createElement('div');
        galleryItem.classList.add('gallery-item');

        const image = item.firstimage ? `<img src="${item.firstimage}" alt="${item.title}">` : '<img src="/images/placeholder.jpg" alt="No Image">';

        galleryItem.innerHTML = `
            <a href="searchresult.html?contentId=${item.contentid}&contentTypeId=${item.contenttypeid}">
                ${image}
                <p>${item.title}</p>
                <p>${item.addr1}</p>
            </a>
        `;
        gallery.appendChild(galleryItem);
    });

    if (itemsToShow.length === 0) {
        gallery.innerHTML = '<p>검색 결과가 없습니다.</p>';
    }
}

function resetFilters() {
    document.querySelectorAll('.checkbox-container input[type="radio"]').forEach(radio => {
        radio.checked = false;
    });

    document.getElementById('region').value = '';
    document.getElementById('sido').innerHTML = '<option value="">시/구/군을 선택하세요</option>';

    const gallery = document.getElementById('gallery');
    gallery.style.display = 'none';
    gallery.innerHTML = ''; // 갤러리 초기화

    const pagination = document.querySelector('.pagination');
    pagination.innerHTML = ''; // 페이지네이션 초기화
    localStorage.removeItem('searchState');  // 저장된 검색 상태 초기화
}

document.querySelector('.search-bar button').addEventListener('click', function() {
    document.getElementById('gallery').style.display = 'grid';
    searchItems();
});

function saveSearchState(region, sigungu, accessibleType, contentTypeId, searchResults) {
    const searchState = {
        region: region,
        sigungu: sigungu,
        accessibleType: accessibleType,
        contentTypeId: contentTypeId,
        searchResults: searchResults
    };
    localStorage.setItem('searchState', JSON.stringify(searchState));
}


function loadSearchState() {
    const savedState = localStorage.getItem('searchState');
    if (savedState) {
        const { region, sigungu, accessibleType, contentTypeId, searchResults } = JSON.parse(savedState);

        // 필터 복원
        document.getElementById('region').value = region;
        updateSido();  // 시/군/구 옵션을 업데이트
        document.getElementById('sido').value = sigungu;
        document.getElementById('accessibleType').value = accessibleType;
        document.querySelector(`input[name="category"][value="${contentTypeId}"]`).checked = true;

        // 검색 결과 복원
        displayGalleryPageFromData(searchResults);
    }
}
function displayGalleryPageFromData(data) {
    const gallery = document.getElementById('gallery');
    gallery.innerHTML = '';  // 기존 갤러리 초기화

    data.forEach(item => {
        const galleryItem = document.createElement('div');
        galleryItem.classList.add('gallery-item');

        const image = item.firstimage ? `<img src="${item.firstimage}" alt="${item.title}">` : '<img src="/images/placeholder.jpg" alt="No Image">';

        galleryItem.innerHTML = `
            <a href="searchresult.html?contentId=${item.contentid}&contentTypeId=${item.contenttypeid}">
                ${image}
                <p>${item.title}</p>
                <p>${item.addr1}</p>
            </a>
        `;
        gallery.appendChild(galleryItem);
    });

    gallery.style.display = 'grid';
}

// 페이지 로드 시 저장된 검색 상태 복원
document.addEventListener('DOMContentLoaded', function() {
    loadSearchState();
});