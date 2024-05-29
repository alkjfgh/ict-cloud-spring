let isSidebarOpen = false
$(document).ready(function () {
    $('.sidebar-open').on('click', function () {
        // $('.sidebar').show();
        $('.sidebar').css('left', '0'); // 사이드바를 오른쪽으로 이동
        isSidebarOpen = true;
    });

    $(document).on('click', function () {
        if(isSidebarOpen){
            const sidebar = $(".sidebar");
            const sidebar_open = $(".sidebar-open");
            if (!sidebar_open.is(event.target) && !sidebar_open.has(event.target).length && !sidebar.is(event.target) && !sidebar.has(event.target).length) {
                // sidebar.hide();
                sidebar.css('left', '-250px'); // 사이드바를 왼쪽으로 이동
            }
        }
    });
});

document.addEventListener('DOMContentLoaded', function() {
    let dropdownMenu = document.querySelector(".dropdown-menu");
    let dropdownButton = document.querySelector(".dropdown-button");

    if (dropdownButton && dropdownMenu) {
        dropdownButton.addEventListener("click", function(event) {
            event.stopPropagation(); // 버블링 방지
            if (dropdownMenu.classList.contains("active")) {
                dropdownMenu.classList.remove("active");
            } else {
                dropdownMenu.classList.add("active");
            }
        });

        document.addEventListener('click', function(event) {
            if (!event.target.closest('.dropdown-menu') && !event.target.closest('.dropdown-button')) {
                dropdownMenu.classList.remove("active");
            }
        });
    } else {
        console.error("dropdownButton 또는 dropdownMenu 요소를 찾을 수 없습니다.");
    }
});