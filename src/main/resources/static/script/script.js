document.querySelector('.search-input').addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault(); // 기본 폼 제출을 막음
        if (this.value.trim() !== '') {
            this.form.submit(); // 폼 제출
        } else {
            alert('Please enter a search term'); // 입력 값이 없을 때 경고창 띄우기
        }
    }
});

// 슬라이드 코드
let currentIndex = 0;
const slides = document.querySelector('.slides');
const dots = document.querySelectorAll('.dot');
const totalSlides = dots.length;

function showSlide(index) {
    currentIndex = (index + totalSlides) % totalSlides;
    const offset = -currentIndex * 100;
    slides.style.transform = `translateX(${offset}%)`;
    updateDots();
}

function updateDots() {
    dots.forEach((dot, index) => {
        dot.classList.toggle('active', index === currentIndex);
    });
}

dots.forEach((dot, index) => {
    dot.addEventListener('click', () => {
        showSlide(index);
    });
});

function showNextSlide() {
    showSlide(currentIndex + 1);
}

// 슬라이드 자동 변경 간격 설정 (5초)
setInterval(showNextSlide, 5000);

updateDots();