<%@ page import="java.sql.*" %>
<%@ page import="org.hoseo.ictcloudspring.connection.DBConnectionPool" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Download</title>
</head>
<body>
<div class="container">
    <div>folder path</div>
    <table>
        <tr>
            <th>NAME</th>
            <th>SIZE</th>
            <th>ACTIONS</th>
        </tr>
        <tr>
            <td>test.txt</td>
            <td>1.28kb</td>
            <td><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6" data-darkreader-inline-stroke="" style="--darkreader-inline-stroke: currentColor;"><path stroke-linecap="round" stroke-linejoin="round" d="M3 16.5v2.25A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75V16.5M16.5 12 12 16.5m0 0L7.5 12m4.5 4.5V3"></path></svg></td>
        </tr>
    </table>
</div>
</body>
</html>
