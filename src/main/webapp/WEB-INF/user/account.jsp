<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<html lang="ko">
<head>
    <title>account</title>
    <meta charset="utf-8">
    <link href="<c:url value="/css/account.css?ver=1"/>" rel="stylesheet" type="text/css">
    <script src="https://code.jquery.com/jquery-3.7.1.js"
            integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
</head>
<body>

<form id="signIn-form" method="post" onsubmit="submitForm(event)">
    <div class="login-box">

        <div class="login-text">
            <h3>로그인</h3>
        </div>

        <div class="login-id">
            <%--@declare id="login-email"--%><label for="login-email">이메일 :</label>
            <input type="email" id="input-login-id" name="email" placeholder="이메일">
        </div>

        <div class="login-pwd">
            <lable for="login-pwd">비밀번호 :</lable>
            <input type="password" id="input-login-pwd" name="password" placeholder="비밀번호">
        </div>

        <div class="loginsignup-button">
            <input type="submit" id="login-login-button" value="로그인">
            <%--<input type="submit" id="login-signup-button" value="회원가입">--%>
            <p>
                <button type="button" id="signupButton">회원가입</button>
            </p>
        </div>
    </div>
</form>

<form action="signUp" method="post">
    <div class="signup-box">

        <div class="signup-text">
            <h3>회원가입</h3>
        </div>

        <div class="signup-name">
            <lable for="signup-name">이름 :</lable>
            <input type="text" id="input-signup-name" name="name" placeholder="이름">
        </div>

        <div class="signup-id">
            <lable for="signup-id">이메일 :</lable>
            <input type="email" id="input-signup-id" name="email" placeholder="이메일">
        </div>

        <div class="signup-pwd">
            <lable for="sighup-pwd">비밀번호 :</lable>
            <input type="password" id="input-signup-pwd" name="password" placeholder="비밀번호">
        </div>

        <%--            비밀번호 확인 기능 추가--%>
        <div class="signup-pwd-check">
            <lable for="signup-confirm-pwd">비밀번호 확인 :</lable>
            <input type="password" id="input-signup-pwd-check" placeholder="비밀번호 확인">
        </div>

        <div class="signup-login-button">
            <input type="submit" value="회원가입" id="signup-signup-button">
            <%--<input type="button" value="로그인" id="signup-login-button">--%>
            <p>
                <button type="button" id="loginButton">로그인</button>
            </p>
        </div>

    </div>
</form>
<script src="<c:url value="/js/account.js?ver=1"/>"></script>
</body>
</html>
