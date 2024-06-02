let isSidebarOpen = false

$(document).ready(function () {
    const islogin = $('.islogin');
    const notlogin = $('.notlogin');

    $('.sidebar-open').on('click', function () {
        $('.sidebar').css('left', '0'); // 사이드바를 오른쪽으로 이동
        isSidebarOpen = true;
    });

    $(document).on('click', function () {
        if (isSidebarOpen) {
            const sidebar = $(".sidebar");
            const sidebar_open = $(".sidebar-open");
            if (!sidebar_open.is(event.target) && !sidebar_open.has(event.target).length && !sidebar.is(event.target) && !sidebar.has(event.target).length) {
                sidebar.css('left', '-250px'); // 사이드바를 왼쪽으로 이동
            }
        }
    });

    axios.get('/user/checkSession').then(res => {
        if (res.status === 200) {
            if (res.data.check){
                console.log(res.data);
                $('.islogin .username').text(res.data.username);
                islogin.show();
            }
            else notlogin.show();
        } else {
            notlogin.show();
        }
    });
});

function formmatDate(created_at) {
    const date = new Date(created_at);
    const year = date.getUTCFullYear();
    const month = String(date.getUTCMonth() + 1).padStart(2, '0');
    const day = String(date.getUTCDate()).padStart(2, '0');
    const hours = String(date.getUTCHours()).padStart(2, '0');
    const minutes = String(date.getUTCMinutes()).padStart(2, '0');
    const seconds = String(date.getUTCSeconds()).padStart(2, '0');

    const formattedDate = `${year}년 ${month}월 ${day}일`;
    const formattedTime = `${hours}:${minutes}:${seconds}`;

    return [formattedDate, formattedTime];
}

const initAnnounce = (announdata) => {
    let announcementContainer = $('.announcement-container');
    announcementContainer.empty();

    let accordionId = 'accordionExample';
    let accordion = document.createElement('div');
    accordion.className = 'accordion';
    accordion.id = accordionId;

    for (let index = announdata.length - 1; index >= 0; index--) {
        let item = announdata[index];
        let uniqueId = `collapse${index}`;
        let announcement = document.createElement('div');
        const formatedDate = formmatDate(item.created_at);
        announcement.className = 'accordion-item';
        announcement.innerHTML = `
                <h2 class="accordion-header">
                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#${uniqueId}" aria-expanded="false" aria-controls="${uniqueId}">
                        <span class="accordion-name" title="${item.title}">${item.title}</span> <span class="accordion-date">${formatedDate[0]}<br>${formatedDate[1]}</span>
                    </button>
                </h2>
                <div id="${uniqueId}" class="accordion-collapse collapse" data-bs-parent="#${accordionId}">
                    <div class="accordion-body">
                        <strong>${item.title}</strong> <br><br>
                        ${item.content}
                    </div>
                </div>`;

        accordion.appendChild(announcement);
    }
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

    // 아코디언이 펼쳐질 때 높이 애니메이션 적용
    $('.accordion-collapse').on('show.bs.collapse', function () {
        $(this).find('.accordion-body').css('max-height', '400px');
    });

    $('.accordion-collapse').on('hide.bs.collapse', function () {
        $(this).find('.accordion-body').css('max-height', '0');
    });
}

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
    let announcementModal = $("#announcementModal");
    let announcementLink = $(".announcement-link");
    let announcementCloseBtn = $(".close")[0];

    axios.get("/notice/getList").then(res => {
        if(res.status === 200){
            console.log(res.data);
            initAnnounce(res.data);
        }
    });

    announcementLink.on('click', function (event) {
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

        announcementModal.css("display", "block");
    });

    announcementCloseBtn.onclick = function () {
        announcementModal.css("display", "none");
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
        if (event.target === announcementModal[0]) {
            announcementModal.css("display", "none");

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
        if(response.status === 200){
            location.href = "../main";
        }
    });
});