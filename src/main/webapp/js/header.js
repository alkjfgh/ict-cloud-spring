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

let dropdownMenu = document.querySelector(".dropdown-menu")
let dropdownButton = document.querySelector(".dropdown-button")

dropdownButton.addEventListener("click", function(event) {
    if (this.active) {
        dropdownMenu.classList.remove("active")
    } else {
        dropdownMenu.classList.add("active")
    }

    this.active = !this.active
})

dropdownButton.active = false