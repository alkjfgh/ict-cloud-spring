let isSidebarOpen = false

let announdata = [
    {date: "2024-05-27 04:07:45", title: "가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하", content: "히브리어 원문 성서에도 밑줄 친 부분의 내용은 나와 있지 않다. 따라서 <공동번역>의 밑줄 친 부분은 히브리어 본문의 반영이 아니라 그리스어 칠십인역의 본문을 번역한 것이다.\n" +
            "\n" +
            "그리스어 칠십인역 성서는 기원전 3세기경부터 번역되기 시작한 것으로서 그때 사용된 히브리어 본문은 지금 남아 있는 히브리어 맛소라 본문보다 약 천 년이나 앞선 본문으로서 원본과 시간상으로 아주 가깝다는 것이 특징이다. 히브리어 맛소라 본문과 그리스어 칠십인역 사이에는 단순한 원본과 번역본의 차이 외에도 내용과 편집사의 차이도 현격하다.\n" +
            "\n" +
            "여기에 제시된 사무엘상 14장 41절은 본문의 전달 과정에서 생긴 오류를 보여주는 대표적인 경우이다. 히브리어 본문이 전달된 형식 가운데 하나가 바로 베껴 쓰기였다. 베껴 쓸 때는 한 사람이 원본을 큰 소리로 읽어주면 여러 명의 베껴 쓰는 사람들이 그것을 받아쓰곤 하였다.\n" +
            "\n" +
            "때로는 베껴 쓰는 사람이 직접 원본을 보고 베끼기도 하였다. 그런데 불러주거나 베껴 쓰는 과정에서 읽는 이가 착각을 일으켜 같은 본문을 두 번 읽으면 중복오자(重複誤字, dittography)가 생기고, 줄을 놓쳐 어느 부분을 빼놓고 읽으면 탈락 현상이 생기기도 한다.\n" +
            "\n" +
            "탈락 현상이 생기는 주요 원인들 중 두 가지가 특히 유명하다. 하나는 유사문미(類似文尾)이고 다른 하나는 우사문두(類似文頭)이다. 몇 개의 문장으로 구성된 어느 글 안에 유사한 단어로 시작되는 문장들이 잇거나 혹은 유사한 단어로 끝나는 문장들이 있을 때 이런 현상이 자주 일어난다.\n" +
            "\n" +
            "사무엘상 14장 41절의 경우는 본래의 히브리어 원문에서 ‘이스라엘’이라는 낱말이 세 번 나오는데 복사 과정에서 첫 번째로 나오는 ‘이스라엘’을 읽은 다음에 그 읽는 이의 눈이 몇 줄을 뛰어 넘어 세 번째 나오는 ‘이스라엘’을 읽어버렸기 때문에 그 사이에 있는 글자들이 모두 빠져버린 것이다."},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
];

$(document).ready(function () {
    const islogin = $('.islogin');
    const notlogin = $('.notlogin');

    $('.sidebar-open').on('click', function () {
        // $('.sidebar').show();
        $('.sidebar').css('left', '0'); // 사이드바를 오른쪽으로 이동
        isSidebarOpen = true;
    });

    $(document).on('click', function () {
        if (isSidebarOpen) {
            const sidebar = $(".sidebar");
            const sidebar_open = $(".sidebar-open");
            if (!sidebar_open.is(event.target) && !sidebar_open.has(event.target).length && !sidebar.is(event.target) && !sidebar.has(event.target).length) {
                // sidebar.hide();
                sidebar.css('left', '-250px'); // 사이드바를 왼쪽으로 이동
            }
        }
    });

    axios.get('/user/checkSession').then(res => {
        if (res.status === 200) {
            if (res.data.check) islogin.show();
            else notlogin.show();
        } else {
            notlogin.show();
        }
    });
});

const initAnnounce = (announdata) => {
    let announcementContainer = $('.announcement-container');
    announcementContainer.empty();

    let accordionId = 'accordionExample';
    let accordion = document.createElement('div');
    accordion.className = 'accordion';
    accordion.id = accordionId;

    announdata.forEach((item, index) => {
        let uniqueId = `collapse${index}`;
        let announcement = document.createElement('div');
        announcement.className = 'accordion-item';
        announcement.innerHTML = `
                <h2 class="accordion-header">
                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#${uniqueId}" aria-expanded="false" aria-controls="${uniqueId}">
                        <span class="accordion-name" title="${item.title}">${item.title}</span> <span class="accordion-date">${item.date}</span>
                    </button>
                </h2>
                <div id="${uniqueId}" class="accordion-collapse collapse" data-bs-parent="#${accordionId}">
                    <div class="accordion-body">
                        <strong>${item.title}</strong> <br><br>
                        ${item.content}
                    </div>
                </div>`;

        accordion.appendChild(announcement);
    });
    announcementContainer.append(accordion);

    //accordion-name이 text-overflow 될때 만 마우스 오버 체크
    $('.accordion-name').each(function() {
        if (this.offsetWidth < this.scrollWidth) {
            $(this).addClass('tooltip-visible');
        }

        if ($(this).hasClass('tooltip-visible')) {
            $(this).attr('data-title', $(this).text());
        } else {
            $(this).removeAttr('data-title');
        }
    });
}

$(document).ready(function() {
    initAnnounce(announdata);
});

// <div className="announcement-title-container">
//     <span className="announcement-title">${item.title}</span>
//     <span className="announcement-uploaddate">${item.date}</span>
// </div>
// <div className="announcement-content-container">
//     <div className="announcement-content hide">${item.content}</div>
// </div>

document.addEventListener('DOMContentLoaded', function () {
    let dropdownMenu = document.querySelector(".dropdown-menu");
    let dropdownButton = document.querySelector(".dropdown-button");

    if (dropdownButton && dropdownMenu) {
        dropdownButton.addEventListener("click", function (event) {
            event.stopPropagation(); // 버블링 방지
            if (dropdownMenu.classList.contains("active")) {
                dropdownMenu.classList.remove("active");
            } else {
                dropdownMenu.classList.add("active");
            }
        });

        document.addEventListener('click', function (event) {
            if (!event.target.closest('.dropdown-menu') && !event.target.closest('.dropdown-button')) {
                dropdownMenu.classList.remove("active");
            }
        });

        dropdownMenu.addEventListener('click', function (event){
            if(event.target.tagName === 'A'){
                dropdownMenu.classList.remove("active");
            }
        })
    } else {
        console.error("dropdownButton 또는 dropdownMenu 요소를 찾을 수 없습니다.");
    }
});

// 모달
$(document).ready(function () {
    // 모달 제어 코드
    let modal = $("#announcementModal");
    let btn = $(".dropdown-menu a[href='#']");
    let span = $(".close")[0];

    initAnnounce(announdata);

    btn.click(function (event) {
        event.preventDefault(); // 기본 이벤트 방지

        $(".accordion-collapse").each(function() {
            let collapseInstance = bootstrap.Collapse.getInstance(this);
            if (collapseInstance) {
                collapseInstance.hide();
            } else {
                new bootstrap.Collapse(this, {
                    toggle: false
                }).hide();
            }
        });

        modal.css("display", "block");
    });

    span.onclick = function () {
        modal.css("display", "none");
        $(".accordion-collapse").each(function() {
            let collapseInstance = bootstrap.Collapse.getInstance(this);
            if (collapseInstance) {
                collapseInstance.hide();
            } else {
                new bootstrap.Collapse(this, {
                    toggle: false
                }).hide();
            }
        });
    }

    $(window).click(function (event) {
        if (event.target === modal[0]) {
            modal.css("display", "none");

            $(".accordion-collapse").each(function() {
                let collapseInstance = bootstrap.Collapse.getInstance(this);
                if (collapseInstance) {
                    collapseInstance.hide();
                } else {
                    new bootstrap.Collapse(this, {
                        toggle: false
                    }).hide();
                }
            });
        }
    });

    $('.logout-link').on('click', async (event) => {
        event.preventDefault();

        const response = await axios.get("/user/logout");
        console.log(response);
        if(response.status === 200){
            location.href = "../main";
        }
    });
});