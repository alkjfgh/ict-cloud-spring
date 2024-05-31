let isSidebarOpen = false

let announdata = [
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
    {date: "2024-05-27 04:07:45", title: "제목", content: "본문"},
];

$(document).ready(function () {
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

    initAnnounce(announdata);

    $('.announcement-title-container').on('click', function () {
        console.log(this);
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
                        ${item.title} ${item.date}
                    </button>
                </h2>
                <div id="${uniqueId}" class="accordion-collapse collapse" data-bs-parent="#${accordionId}">
                    <div class="accordion-body">
                        ${item.content}
                    </div>
                </div>`;

        accordion.appendChild(announcement);
    });
    announcementContainer.append(accordion);
}

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
});