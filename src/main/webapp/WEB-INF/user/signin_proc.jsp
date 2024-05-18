<%@ page import="org.hoseo.ictcloudspring.dao.UserService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<jsp:useBean id="user" class="org.hoseo.ictcloudspring.dto.User"/>
<jsp:setProperty name="user" property="*"/>
<%
    request.setCharacterEncoding("UTF-8");
    out.println(user);

    UserService usc = new UserService(application);

    boolean loggedIn = usc.checkSignIn(user);

//    로그인 성공시 로그인 유지 가능해야함
//    todo 로그인 성공시 로그인 유지 기능
    if(loggedIn){
        out.print("로그인 성공" + user.getEmail());
    }
    else{
        out.print("<h2>로그인 실패 : 올바른 이메일과 비밀번호를 입력하세요.</h2>");
    }
%>
