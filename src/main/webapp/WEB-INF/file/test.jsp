<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.hoseo.ictcloudspring.dto.File" %>

<%
    File file = (File) request.getAttribute("file");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>File Upload Result</title>
</head>
<body>
<h1>File Upload Result</h1>
<p><%=file%></p>
<!-- 기타 필요한 정보 출력 -->
</body>
</html>