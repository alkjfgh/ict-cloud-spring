<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <link href='https://fonts.googleapis.com/css?family=Roboto:300' rel='stylesheet'>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <link rel="stylesheet" href="<c:url value="/css/userInfo.css?ver=1"/>">
    <title>userinfo</title>
</head>
<body>
<jsp:include page="../header.jsp"/>
<jsp:include page="../sidebar.jsp"/>
<div class="container-fluid user-profile">
    <div class="row">
        <div class="col-xs-12">
            <div class="well well-sm">
                <div class="user-profile-card">
                    <div class="info-header">
                        <h1>User Information</h1>
                    </div>
                    <div class="row">
                        <div class="container">
                            <h4>Name</h4>
                            <small></small>
                            <br/>
                        </div>
                        <div class="container">
                            <h4>Email</h4>
                            <small></small>
                            <br/>
                        </div>
                        <div class="container">
                            <h4>JoinDate</h4>
                            <small></small>
                            <br>
                        </div>

                    </div>
                    <div class="information">
                        <a href="#" data-bs-toggle="modal" data-bs-target="#exampleModal">Change Password</a> |
                        <a href="#">Unsubscribing</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- 모달 -->
    <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">Change Password</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <lable><h4>Existing Password</h4></lable>
                    <input type="password" placeholder="existing password" class="input-ex-pwd" name="password"
                           required>
                    <br>
                    <lable><h4>Change Password</h4></lable>
                    <input type="password" placeholder="password" class="input-signup-pwd" name="password" required/>
                    <br>
                    <lable><h4>Change Password Check</h4></lable>
                    <input type="password" placeholder="password check" class="input-signup-pwd-check" name="password"
                           required/>
                    <br>
                </div>
                <div class="modal-footer">
                    <button id="close-modal" type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button id="change-password" type="button" class="btn btn-dark">Confirm Change</button>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL"
        crossorigin="anonymous"></script>
<script src="<c:url value="/js/userInfo.js"/>"></script>
</html>
