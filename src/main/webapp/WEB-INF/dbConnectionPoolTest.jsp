<%@ page import="java.sql.*" %>
<%@ page import="org.hoseo.ictcloudspring.connection.DBConnectionPool" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<%
    Connection conn = null;
    Statement stmt = null;
    try {
        // 데이터베이스에 연결합니다.
        conn = new DBConnectionPool(application).getConnection();
        // 연결 성공 메시지를 출력합니다.
        out.println("<h2>데이터베이스 연결 성공!</h2>");
        // 간단한 쿼리를 실행합니다.
        stmt = conn.createStatement();
        String query = "SELECT version();";
        ResultSet rs = stmt.executeQuery(query);
        if(rs.next()) {
            out.println("<p>데이터베이스 버전: " + rs.getString(1) + "</p>");
        }
        rs.close();
    } catch (SQLException e) {
        out.println("데이터베이스 연결 실패: " + e.getMessage());
    } finally {
        // 자원을 해제합니다.
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
%>
</body>
</html>
