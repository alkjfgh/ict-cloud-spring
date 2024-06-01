<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="<c:url value='/css/header.css?ver=1'/>"/>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
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
                    <a href="" class="logout-link">
                        Log out
                    </a>
                </li>
                <li id="announcement-link">
                    <a href="#">
                        Announcement
                    </a>
                </li>
            </ul>
        </div>

        <%--    로그인 안됐을 때--%>
        <div class="notlogin">
            <h3>
                Sign in to <strong>ICT-CLOUD</strong>
            </h3>

            <ul>
                <li>
                    <a href="<c:url value="/user/account"/>">
                        Sign in
                    </a>
                </li>
                <li>
                    <a href="<c:url value="/user/account?c=1"/>">
                        Sign up
                    </a>
                </li>
                <li>
                    <a href="#">
                        Announcement
                    </a>
                </li>
            </ul>
        </div>
    </div>
</div>

<%--    모달--%>
<div id="announcementModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2>Announcement</h2> <br>
        <div class="announcement-container">
<%--            <div class="announcement-list">--%>
<%--                <div class="-announcement-title-container">--%>
<%--                    <span class="announcement-title">title</span>--%>
<%--                    <span class="announcement-uploaddate">date</span>--%>
<%--                </div>--%>
<%--                <div class="announcement-content-container">--%>
<%--                    <div class="announcement-content">content</div>--%>
<%--                </div>--%>
<%--            </div>--%>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous">
</script>