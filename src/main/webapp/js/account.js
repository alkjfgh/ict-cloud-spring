$(document).ready(() => {
    $('#signupButton').click(function () { //회원가입 버튼을 누르면
        $('.login-box').hide() //로그인 박스를 숨김
        $('.signup-box').show() //회원가입 버튼을 보여줌
        $('#loginButton').show() //로그인 버튼을 보여줌
    });

    $('#loginButton').click(() => { //로그인 버튼을 누르면
        $('.signup-box').hide() //회원가입 박스를 숨김
        $('#loginButton').hide() //로그인 버튼을 숨김
        $('.login-box').show() //로그인 박스를 보여줌
    });

    $('#signIn-form').submit(function (event) {
        event.preventDefault(); // 기본 폼 제출 동작을 막습니다.

        $.ajax({
            url: '/user/signIn',
            type: 'POST',
            data: $(this).serialize(), // 폼 데이터를 직렬화합니다.
            success: function (response) {
                if (response.status === 'success') {
                    window.location.href = '../file/upload'; // 로그인 성공 시 페이지 이동
                } else {
                    alert(response.message); // 로그인 실패 시 메시지 표시
                }
            },
            error: function (xhr, status, error) {
                alert('로그인 요청 중 오류가 발생했습니다.');
            }
        });
    });
});