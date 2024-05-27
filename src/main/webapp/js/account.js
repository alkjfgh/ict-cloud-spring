emailjs.init('JK8Ed-9EvY0p1umu2')

const signInSubmit = (event) => {
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

//이름이 영어 형식인지 검사
function isValidName(name) {
    let eng = /^[a-zA-Z]*$/;
    return eng.test(name);
}

//패스워드 [조건]에 맞는지 검사
function isValidPassword(password) {
    let passwd_regex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,16}$/;
    return passwd_regex.test(password);
}

//이메일 형식인지 검사
function isValidEmail(email) {
    let email_regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;
    return email_regex.test(email);
}

const signUpSubmit = async (event) => {
    event.preventDefault(); //submit을 막음

    let name = $(".input-signup-name").val();
    let email = $(".input-signup-id").val();
    let password = $(".input-signup-pwd").val();

    if(!isValidName(name)){
        alert("Please enter your name in English only.");
        return false;
    }

    // 이메일 정규식 검사
    if (isValidEmail(email)) {
        alert("Please format your email accordingly.")
        return false;
    }

    if (isValidPassword(password)) {
        alert("Please enter the password in the correct format.")
        return false;
    }

    // 비밀번호 검사
    let checkpwd = $(".input-signup-pwd-check").val();
    if (password !== checkpwd) {
        alert("The passwords don't match.");
        return false;
    }

    $('.register-area').hide();
    $('.email-verification').show();

    sendVerificationEmail()
}

$(document).ready(function () {
    $('.message a').click(function () {
        $('form').animate({height: "toggle", opacity: "toggle"}, "slow");
        $('.register-area').show();
        $('.email-verification').hide();
    });

    $('.input-signup-name').on('propertychange change keyup paste input', function () {
        const name = $(this).val();
        if(name === '') {
            $(this).removeClass('invalid');
            return;
        }
        if(isValidName(name)) $(this).removeClass('invalid');
        else $(this).addClass('invalid');
    });

    $('.input-signup-pwd').on('propertychange change keyup paste input', function () {
        const pwd = $(this).val();
        if(pwd === '') {
            $(this).removeClass('invalid');
            return;
        }
        if(isValidPassword(pwd)) $(this).removeClass('invalid');
        else $(this).addClass('invalid');
    });

    $('.input-signup-pwd-check').on('propertychange change keyup paste input', function () {
        const pwd = $('.input-signup-pwd').val();
        const pwdCheck = $(this).val();
        if(pwdCheck === '') {
            $(this).removeClass('invalid');
            return;
        }
        if(isValidPassword(pwdCheck)) $(this).removeClass('invalid');
        else $(this).addClass('invalid');

        if(pwd !== pwdCheck) $(this).addClass('invalid');
    });

    $('.input-signup-id').on('propertychange change keyup paste input', function () {
        const email = $(this).val();
        if(email === '') {
            $(this).removeClass('invalid');
            return;
        }
        if(isValidEmail(email)) $(this).removeClass('invalid');
        else $(this).addClass('invalid');
    });
});

const sendVerificationEmail = async () => {
    const serviceID = 'default_service';
    const templateID = 'template_j82ka3n';
    const email = $('.input-signup-id').val()
    const user_name = $('.input-signup-name').val()

    // 랜덤한 토큰 생성
    const token = await generateRandomToken(); // 이 함수는 나중에 구현

    if (token === null) return;

    // 이메일 내용 설정
    const emailParams = {
        to_email: email,
        token: token,
        user_name: user_name
    };

    // emailjs를 사용하여 이메일 전송
    emailjs.send(serviceID, templateID, emailParams)
        .then(() => {
            alert('Verification email sent!');
            // 이메일이 성공적으로 전송되었을 때 추가 작업 수행
        })
        .catch((error) => {
            console.error('Error sending verification email:', error);
            alert('Failed to send verification email.');
        });
};

const validateEmail = async () => {
    const email = $('.input-signup-id').val();
    const input_token = $('.email-token').val();

    const reponse = await axios.get('/user/getToken?email=' + email);
    let token = null;

    if (reponse.status === 200) {
        token = reponse.data.token;
    } else {
        console.error("something error happened...");
    }

    if (token === null) return;
    if (token !== input_token) {
        alert('Invalid Token');
        return;
    }

    let response = await axios.get("/user/deleteToken?email=" + email);
    if (response.status === 200) {
        console.log("delete token ok");
        let name = $(".input-signup-name").val();
        let password = $(".input-signup-pwd").val();

        response = await axios.post("/user/signUp", {name: name, password: password, email: email});
        if (response.status === 200) {
            alert("SignUp Complete!");
            window.location.reload();
        } else {
            alert("Something Error happened");
            location.href = "../main";
        }
    }
}

const generateRandomToken = async () => {
    const email = $('.input-signup-id').val()

    const reponse = await axios.get('/user/generateToken?email=' + email);
    let token = null;

    if (reponse.status === 200) {
        token = reponse.data.token;
    } else {
        console.error("something error happened...");
    }

    return token;
}

// 이메일 실시간 체크
function printEmail() {

    // 이메일 입력창, 이메일 입력조건 불만족 시 하단에 표시되는 경고 텍스트 변수에 할당
    const email = document.getElementById('email_input');
    const email_check = document.getElementById('email_check');

    // 입력창의 값이 이메일 형태와 맞지 않게 입력된 경우
    if(!isEmail(email.value)){

        // 이메일 입력창의 테두리 빨간색으로 변경
        email.style.borderColor = '#EF4444';
        // 입력창 하단의 경고 텍스트 보이게
        email_check.style.visibility = 'visible';
    }
    // 이메일 형태에 적합하게 입력된 경우
    else {
        // 테두리 색 원래대로 변경
        email.style.borderColor = '#9CA3AF';
        // 경고 텍스트 안보이게 처리
        email_check.style.visibility = 'hidden';

        // 로그인 버튼 활성화 여부를 체크하는 함수 선언
        btnCheck()
    }

}