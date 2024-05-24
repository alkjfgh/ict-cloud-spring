emailjs.init('JK8Ed-9EvY0p1umu2')

const signInSubmit = (event) => {
    event.preventDefault(); // 기본 폼 제출 동작을 막습니다.

    $.ajax({
        url: '/user/signIn',
        type: 'POST',
        data: $('.login-form').serialize(), // 폼 데이터를 직렬화합니다.
        success: function (response) {
            if (response.status === 'success') {
                if(response.level == 2) window.location.href = '../admin';
                else window.location.href = '../file/upload'; // 로그인 성공 시 페이지 이동
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
    if (!eng.test(name)) {
        alert("Please enter your name in English only.")
        return false;
    }

    // 비밀번호 검사
    let checkpwd = $(".input-signup-pwd-check").val();
    if (password !== checkpwd) {
        alert("The passwords don't match.");
        return false;
    }

    // 이메일 정규식 검사
    let email_regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;
    if (!email_regex.test(email)) {
        alert("Please format your email accordingly.")
        return false;
    }

    let passwd_regex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,16}$/;
    if (!passwd_regex.test(password)) {
        alert("Please enter the password in the correct format.")
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