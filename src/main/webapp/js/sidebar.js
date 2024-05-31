$(document).ready(() => {
    const currentUrl = window.location.pathname;
    console.log(currentUrl);

    $('.sidebar-menu li a').each(function () {
        const linkUrl = $(this).attr('href');

        if (currentUrl === linkUrl) {
            $(this).closest('li a').addClass('active');
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

    axios.get('/user/checkSession').then(res => {
        if (res.status === 200) {
            if (res.data.check) $('.sidebar-menu li:nth-of-type(2)').show();
        }
    });
});

// 서브메뉴 토글 예제 (서브메뉴가 있는 경우)
// $('.sidebar-menu li.has-submenu > a').click(function(e) {
//     e.preventDefault();
//     $(this).next('.submenu').slideToggle();
//     $(this).parent().toggleClass('open');
// });