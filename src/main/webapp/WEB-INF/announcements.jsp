<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Announcements</title>
</head>
<body>
<table class="table">
    <tr>
        <td colspan="2" class="header-text"><h2>Announcements</h2></td>
    </tr>
    <tr class="header">
        <td class="num">number</td>
        <td class="title">title</td>
        <td>author</td>
        <td>written date</td>
    </tr>
</table>
<br>
<table>
    <tr>
        <td>
<%--        todo display : none 으로 숨겨 놓았고 작성자 아이디로 로그인 됐을 때만 보이게--%>
            <button class="write-button" onclick="location.href='write.jsp'">writing</button>
        </td>
    </tr>
</table>
</body>
</html>
