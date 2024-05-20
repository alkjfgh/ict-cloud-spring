<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script src="../../js/account.js"></script>
    <link rel="stylesheet" href="../../css.user.css?ver=1">
    <title>user</title>
</head>
<body>
<div class="user-box">

    <div class="user-text">
        <h3>Your account</h3>
    </div>

    <form action="account.jsp">
        <div class="logout">
            <input type="submit" id="logout-button" value="LogOut">
        </div>
    </form>

    <form action="">
        <div class="advanced-setting">
            <input type="submit" id="setting-button" value="Settings">
        </div>
    </form>

    <form action="deleteaccount.jsp">
        <div class="delete-user">
            <input type="submit" id="delete-button" value="Delete Account" onclick="deleteaccount()">
        </div>
    </form>

</div>
</body>
</html>
