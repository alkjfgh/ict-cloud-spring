<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="<c:url value='/css/header.css?ver=1'/>"/>

<script src="<c:url value='/js/header.js?ver=1'/>"></script>

<div class="header">
    <div class="sidebar-open">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
             class="inline-block w-6 h-6 stroke-current">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
        </svg>
    </div>

    <div class="user">
        <input type="button" class="dropdown-button">
    </div>

    <div class="dropdown-menu">
        <%--    로그인 됐을 때--%>
        <div class="islogin">
            <h3>
                Signed in as <strong class="username">username</strong>
            </h3>

            <ul>
                <li>
                    <a href="<c:url value="/user/info"/>">
                        Your profile
                    </a>
                </li>
                <li>
                    <a href="#">
                        Sign out
                    </a>
                </li>
            </ul>
        </div>

        <%--    로그인 안됐을 때--%>
        <div class="notlogin">
            <h3>
                Sign in to <strong>ICTcloud</strong>
            </h3>

            <ul>
                <li>
                    <a href="<c:url value="/user/account"/>">
                        Login
                    </a>
                </li>
                <li>
                    <a href="<c:url value="/user/account#"/>">
                        Sign in
                    </a>
                </li>
            </ul>
        </div>
    </div>
</div>