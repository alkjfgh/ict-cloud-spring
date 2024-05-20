<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script src="<c:url value="/js/account.js"/>"></script>
    <link rel="stylesheet" href="<c:url value="/css/user.css?ver=1"/>">
    <title>user</title>
</head>
<body>
<div class="user-box">

    <div class="user-text">
        <h3>Your account</h3>
    </div>

    <form>
        <div class="logout">
            <input type="submit" id="logout-button" value="Log Out">
        </div>
    </form>

    <form>
        <div class="advanced-setting">
            <input type="submit" id="setting-button" value="Settings">
        </div>
    </form>

    <form>
        <div class="delete-user">
            <input type="submit" id="delete-button" value="Delete Account" onclick="deleteaccount()">
        </div>
    </form>

</div>
</body>
</html>
