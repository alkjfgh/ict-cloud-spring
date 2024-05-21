// $('.logIn-form').submit(function (event) {
const submitForm = (event) => {
    event.preventDefault(); // 기본 폼 제출 동작을 막습니다.

    $.ajax({
        url: '/user/signIn',
        type: 'POST',
        data: $('.login-form').serialize(), // 폼 데이터를 직렬화합니다.
        success: function (response) {
            if (response.status === 'success') {
                window.location.href = '../file/upload'; // 로그인 성공 시 페이지 이동
            } else {
                alert(response.message); // 로그인 실패 시 메시지 표시
            }
        },
        error: function (xhr, status, error) {
            alert('An error occurred during the login request.');
        }
    });
}


const signUpSubmit = async (event) => {
    event.preventDefault(); //submit을 막음

    let name = $(".input-signup-name").val();
    let email = $(".input-signup-id").val();
    let password = $(".input-signup-pwd").val();

    //이름 영어만 입력 가능하도록
    let eng = /^[a-zA-Z]*$/;
    if(!eng.test(name)){
        alert("Please enter your name in English only.")
        return false;
    }

    // 비밀번호 검사
    let checkpwd = $(".input-signup-pwd-check").val();
    if(password !== checkpwd){
        alert("The passwords don't match.");
        return false;
    }

    // 이메일 정규식 검사
    let email_regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;
    if(!email_regex.test(email)){
        alert("Please format your email accordingly.")
        return false;
    }

    let passwd_regex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,16}$/;
    if(!passwd_regex.test(password)){
        alert("Please enter the password in the correct format.")
        return false;
    }
    return true;
}

$(document).ready(function(){
    $('.message a').click(function(){
        $('form').animate({height: "toggle", opacity: "toggle"}, "slow");
    });
});