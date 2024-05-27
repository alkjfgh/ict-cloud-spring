<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>">
    <title>index</title>
</head>
<body>
<jsp:include page="header.jsp"/>
<jsp:include page="sidebar.jsp"/>
<div class="main-body">
    <div class="body-header">
        <span class="bold-text">ICT</span><span class="next-text">cloud</span>
    </div>
    <div class="header-text">
        <p>technical demonstration is now available</p>
    </div>

    <div class="boxes">
        <div class="first-box">
            <strong><span class="body-text">Scalability and Flexibility</span></strong> <br>
            <span class="body-addtext">Cloud computing allows businesses to
                scale resources up or down based on demand, providing
                unparalleled flexibility.</span>
        </div>

        <div class="second-box">
            <strong><span class="body-text">Cost Efficiency</span></strong> <br>
            <span class="body-addtext">By utilizing cloud services, companies
                can reduce their IT infrastructure costs and pay only for
                what they use.</span>
        </div>

        <div class="third-box">
            <strong><span class="body-text">Enhanced Collaboration</span></strong> <br>
            <span class="body-addtext">Cloud solutions enable seamless collaboration
                across different locations, improving productivity and connectivity
                for remote teams.</span>
        </div>
    </div>
</div>
</body>
</html>
