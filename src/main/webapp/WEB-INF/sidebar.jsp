<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="<c:url value='/css/sidebar.css?ver=1'/>"/>

<script src="<c:url value='/js/sidebar.js?ver=1'/>"></script>

<div class="sidebar">
    <div class="sidebar-header">
        <h2><a href="<c:url value="/main"/>">ICT CLOUD</a></h2>
    </div>
    <ul class="sidebar-menu">
        <li><a href="<c:url value="/user/info"/>">User info</a></li>
        <li><a href="<c:url value="/file/upload"/>">Dashboard</a></li>
        <li><a href="#" class="logout-link">Logout</a></li>
    </ul>
</div>