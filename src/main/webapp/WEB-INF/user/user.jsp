<%--
  Created by IntelliJ IDEA.
  User: lhc99
  Date: 24. 5. 10.
  Time: 오전 8:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>user</title>
</head>
<body>
    <div>

        <div class="user-text">
            <h3>당신의 계정</h3>
        </div>

        <form action="account.jsp">
            <div class="logout">
                <input type="submit" id="logout-button" value="로그아웃">
            </div>
        </form>

        <form action="">
            <div class="advanced-setting">
                <input type="submit" id="setting-button" value="고급 설정">
            </div>
        </form>

        <form action="deleteaccount.jsp">
            <div class="delete-user">
                <input type="submit" id="delete-button" value="계정 삭제">
            </div>
        </form>

    </div>
</body>
</html>
