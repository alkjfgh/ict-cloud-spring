<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<html lang="en">
<head>
    <title>account</title>
    <meta charset="utf-8">
    <%--    <script src="https://cdn.jsdelivr.net/npm/less" ></script>--%>
    <%--    <link rel="stylesheet" href="account.less?ver=1">--%>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <link href='https://fonts.googleapis.com/css?family=Nunito:600,700&display=swap' rel='stylesheet'>
    <link rel="stylesheet" href="../../css/account.css?ver=1">
    <script src="../../js/account.js?ver=1"></script>
</head>
<body>
<div class="login-page">
    <div class="form">
        <form class="register-form" action="signUp" method="post" name="signup_form" onsubmit="return signUpSubmit(event)">
            <input type="text" placeholder="name" class="input-signup-name" name="name" required/>
            <input type="password" placeholder="password" class="input-signup-pwd" name="password" required/>
            <input type="password" placeholder="password check" class="input-signup-pwd-check" name="password" required/>
            <input type="email" placeholder="email address" class="input-signup-id" name="email" required/>
            <input type="submit" class="login-signup-button" value="create"/>
            <p class="message">Already registered? <a href="#">Sign In</a></p>
        </form>
        <form class="login-form" method="post" onsubmit="submitForm(event)">
            <input type="text" placeholder="email address" class="input-login-id" name="email" required/>
            <input type="password" placeholder="password" class="input-login-pwd" name="password" required/>
            <input type="submit" class="login-login-button" value="login"/>
            <p class="message">Not registered? <a href="#">Create an account</a></p>
        </form>
    </div>
</div>


<%--    <form action="signin_proc.jsp" method="post">--%>
<%--        <div class="login-box">--%>

<%--            <div class="login-text">--%>
<%--                <h2>Login</h2>--%>
<%--            </div>--%>

<%--            <div class="login-id">--%>
<%--                &lt;%&ndash;@declare id="login-email"&ndash;%&gt;<label for="login-email">E-mail :</label>--%>
<%--                <input type="email" class="input-login-id" name="email" placeholder="e-mail">--%>
<%--            </div>--%>

<%--            <div class="login-pwd">--%>
<%--                <lable for="login-pwd">Password :</lable>--%>
<%--                <input type="password" class="input-login-pwd" name="password" placeholder="password">--%>
<%--            </div>--%>

<%--            <div class="loginsignup-button">--%>
<%--                <input type="submit" class="login-login-button" value="Login">--%>
<%--                &lt;%&ndash;<input type="submit" class="login-signup-button" value="회원가입">&ndash;%&gt;--%>
<%--                <p><button type="button" class="signupButton">SignUp</button></p>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--    </form>--%>

<%--    <form action="signup_proc.jsp" method="post" onsubmit="return signUpSubmit(event)" name="signup_form">--%>
<%--        <div class="signup-box">--%>

<%--            <div class="signup-text">--%>
<%--                <h3>SignUp</h3>--%>
<%--            </div>--%>
<%--&lt;%&ndash;            TODO 정규식 검사 해야함&ndash;%&gt;--%>

<%--            <div class="signup-name">--%>
<%--                <lable for="signup-name">Name :</lable>--%>
<%--                <input type="text" class="input-signup-name" name="name" placeholder="name" required>--%>
<%--            </div>--%>

<%--            <div class="signup-id">--%>
<%--                <lable for="signup-id">E-mail :</lable>--%>
<%--                <input type="email" class="input-signup-id" name="email" placeholder="e-mail" required>--%>
<%--            </div>--%>

<%--            <div class="signup-pwd">--%>
<%--                <lable for="sighup-pwd">Password :</lable>--%>
<%--                <input type="password" class="input-signup-pwd" name="password" placeholder="password" required>--%>
<%--            </div>--%>

<%--            <div class="signup-pwd-check">--%>
<%--                <lable for="signup-confirm-pwd">Confirm Password :</lable>--%>
<%--                <input type="password" class="input-signup-pwd-check" placeholder="Confirm Password" required>--%>
<%--            </div>--%>
<%--            <div class="signup-login-button">--%>
<%--                <input type="submit" value="Signup" class="signup-signup-button">--%>
<%--                &lt;%&ndash;<input type="button" value="로그인" id="signup-login-button">&ndash;%&gt;--%>
<%--                <p><button type="button" class="loginButton">Login</button></p>--%>
<%--            </div>--%>

<%--        </div>--%>
<%--    </form>--%>
</body>
</html>
