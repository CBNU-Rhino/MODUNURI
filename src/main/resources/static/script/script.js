document.addEventListener('DOMContentLoaded', function() {
    let lastScrollY = window.scrollY; // 이전 스크롤 위치 저장

    // 1. Search Input 처리
    const searchInput = document.querySelector('.search-input');
    if (searchInput) {
        searchInput.addEventListener('keydown', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                if (this.value.trim() !== '') {
                    this.form.submit();
                } else {
                    alert('Please enter a search term');
                }
            }
        });
    }

    // 2. Signup Form 처리
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', function(event) {
            event.preventDefault();
            window.location.href = 'signup_complete.html';
        });
    }

    // 3. Login Form 처리
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;

            if (username === "testuser" && password === "password") {
                window.location.href = "index.html";
            } else {
                alert("Invalid username or password");
            }
        });
    }

    // 4. 이미지 스크롤 애니메이션 처리 (차례대로 나타나도록 수정)
    const images = document.querySelectorAll('.image-container .img-item');
    let delay = 0; // 딜레이 초기값 설정

    function checkImages() {
        const windowHeight = window.innerHeight;
        delay = 0; // 스크롤을 내릴 때마다 초기화

        images.forEach((image, index) => {
            const imageTop = image.getBoundingClientRect().top;
            if (imageTop < windowHeight) {
                setTimeout(() => {
                    image.classList.add('show'); // 이미지가 뷰포트에 들어오면 show 클래스 추가
                }, delay); // 각 이미지마다 딜레이를 줌
                delay += 500; // 500ms씩 딜레이 증가
            } else if (imageTop > windowHeight) {
                image.classList.remove('show'); // 스크롤을 올릴 때 show 클래스 제거
            }
        });
    }

    window.addEventListener('scroll', checkImages);
    checkImages(); // 페이지 로드 시에도 실행

    // 5. Additional Content 스크롤 애니메이션 처리
    const additionalContent = document.querySelector('.additional-content');
    function checkAdditionalContent() {
        const windowHeight = window.innerHeight;
        const contentTop = additionalContent.getBoundingClientRect().top;

        if (contentTop < windowHeight - 100) {
            additionalContent.classList.add('show');
        } else if (contentTop > windowHeight) {
            additionalContent.classList.remove('show'); // 스크롤을 올릴 때 숨기기
        }
    }
    window.addEventListener('scroll', checkAdditionalContent);
    checkAdditionalContent();

    // 6. Split-Container 스크롤 애니메이션 처리
    const splitContainer = document.querySelector('.split-container');
    function checkSplitContainer() {
        const windowHeight = window.innerHeight;
        const splitTop = splitContainer.getBoundingClientRect().top;

        if (splitTop < windowHeight - 100) {
            splitContainer.classList.add('show');
        } else if (splitTop > windowHeight) {
            splitContainer.classList.remove('show'); // 스크롤을 올릴 때 숨기기
        }
    }
    window.addEventListener('scroll', checkSplitContainer);
    checkSplitContainer();

    // 7. 비디오 처리
    const videoElement = document.querySelector('.fullscreen-video');
    if (videoElement) {
        videoElement.muted = true;
        videoElement.play().catch(function(error) {
            console.error('Auto-play was prevented:', error);
        });

        videoElement.addEventListener('loadeddata', function() {
            console.log('Video loaded successfully');
        });

        videoElement.addEventListener('error', function(event) {
            console.error('Error loading video:', event);
            alert('There was an issue loading the video.');
        });

        videoElement.addEventListener('play', function() {
            console.log('Video is playing');
        });
    }

    // 8. 비디오 위의 텍스트 애니메이션 처리
    const videoText = document.querySelector('.video-text');
    if (videoText) {
        setTimeout(() => {
            videoText.classList.add('show'); // .show 클래스 추가로 텍스트가 서서히 나타남
        }, 500); // 페이지가 로드된 후 0.5초 뒤에 실행
    }

    // 9. 화살표 아이콘 스크롤 이벤트 처리
    const arrowIcon = document.querySelector('.arrow-icon');

    function checkArrowIcon() {
        const windowHeight = window.innerHeight;
        const arrowTop = arrowIcon.getBoundingClientRect().top;

        // 화살표 아이콘이 화면에 들어오면 show 클래스 추가
        if (arrowTop < windowHeight) {
            arrowIcon.classList.add('show');
        } else {
            // 스크롤 업 시 다시 숨기기
            arrowIcon.classList.remove('show');
        }
    }

    // 트레블 컨테이너의 스크롤 애니메이션 처리
    const travelContainer = document.querySelector('.travel-container');

    function checkTravelContainer() {
        const windowHeight = window.innerHeight;
        const travelTop = travelContainer.getBoundingClientRect().top;

        if (travelTop < windowHeight - 100) {
            travelContainer.classList.add('show');
        } else if (travelTop > windowHeight) {
            travelContainer.classList.remove('show'); // 스크롤을 올릴 때 숨기기
        }
    }

    // 스크롤 애니메이션 처리 함수 (위로 스크롤할 때 요소 숨기기)
    function scrollAnimation() {
        const elements = document.querySelectorAll('.hidden'); // 숨겨진 상태의 요소 선택
        const windowHeight = window.innerHeight;

        elements.forEach(element => {
            const elementTop = element.getBoundingClientRect().top;

            if (elementTop < windowHeight - 100) { // 요소가 화면에 들어왔을 때
                element.classList.add('show'); // show 클래스를 추가해 애니메이션 실행
                element.classList.remove('hidden'); // hidden 클래스 제거
            } else if (elementTop > windowHeight) { // 화면에서 벗어났을 때
                element.classList.remove('show'); // show 클래스를 제거해 사라지게
                element.classList.add('hidden'); // 다시 hidden 클래스로 돌아가기
            }
        });

        // 화살표 아이콘도 스크롤에 반응하도록 호출
        checkArrowIcon();
    }

    // 페이지 로드 시 한 번 실행
    scrollAnimation();

    // 스크롤할 때마다 실행
    window.addEventListener('scroll', scrollAnimation);

    // 페이지 로드 시 처음 실행
    checkArrowIcon();
    checkTravelContainer();

    // 스크롤 시 각 함수 호출
    window.addEventListener('scroll', () => {
        checkArrowIcon();
        checkTravelContainer();
    });

});
