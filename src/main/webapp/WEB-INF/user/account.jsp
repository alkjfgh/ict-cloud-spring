<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<html lang="en">
<head>
    <title>account</title>
    <meta charset="utf-8">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/@emailjs/browser@4/dist/email.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <link href='https://fonts.googleapis.com/css?family=Nunito:600,700&display=swap' rel='stylesheet'>
    <link rel="stylesheet" href="<c:url value="/css/account.css?ver=1"/>">
    <script src="<c:url value="/js/account.js?ver=1"/>"></script>
</head>
<body>
<div class="login-page">
    <div class="form">
        <form class="register-form" action="signUp" method="post" name="signup_form"
              onsubmit="return signUpSubmit(event)">
            <div class="register-area">
                <input type="text" placeholder="name" class="input-signup-name" name="name" required/>
                <input type="password" placeholder="password" class="input-signup-pwd" name="password" required/>
                <input type="password" placeholder="password check" class="input-signup-pwd-check" name="password"
                       required/>
                <input type="email" placeholder="email address" class="input-signup-id" name="email" required/>
                <input type="submit" class="login-signup-button" value="create"/>
            </div>
            <div class="email-verification" style="display: none">
                <input type="text" name="token" class="email-token">
                <button type="button" onclick="
                validateEmail()">verification</button>
            </div>
            <p class="message">Already registered? <a href="#">Sign In</a></p>
        </form>
        <form class="login-form" method="post" onsubmit="signInSubmit(event)">
            <input type="text" placeholder="email address" class="input-login-id" name="email" required/>
            <input type="password" placeholder="password" class="input-login-pwd" name="password" required/>
            <input type="submit" class="login-login-button" value="login"/>
            <p class="message">Not registered? <a href="#">Create an account</a></p>
        </form>
    </div>
</div>
</body>
</html>
