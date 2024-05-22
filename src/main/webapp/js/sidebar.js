$(document).ready(() => {
    const currentUrl = window.location.pathname;
    console.log(currentUrl);

    $('.sidebar-menu li a').each(function () {
        const linkUrl = $(this).attr('href');

        if (currentUrl === linkUrl) {
            $(this).closest('li a').addClass('active');
        }
    });
});

// 서브메뉴 토글 예제 (서브메뉴가 있는 경우)
// $('.sidebar-menu li.has-submenu > a').click(function(e) {
//     e.preventDefault();
//     $(this).next('.submenu').slideToggle();
//     $(this).parent().toggleClass('open');
// });