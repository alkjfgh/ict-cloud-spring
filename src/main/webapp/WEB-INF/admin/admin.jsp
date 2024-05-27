<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>클라우드 스토리지 관리자 페이지</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="<c:url value="/css/admin.css"/>">
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <a class="navbar-brand" href="../admin">관리자 페이지</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
            aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav">
            <li class="nav-item active" data-target="#dashboard">
                <a class="nav-link" href="#">대시보드</a>
            </li>
            <li class="nav-item" data-target="#user-management">
                <a class="nav-link" href="#">사용자 관리</a>
            </li>
            <li class="nav-item" data-target="#file-management">
                <a class="nav-link" href="#">파일 관리</a>
            </li>
            <li class="nav-item" data-target="#storage-usage">
                <a class="nav-link" href="#">스토리지 사용량</a>
            </li>
            <li class="nav-item" data-target="#settings">
                <a class="nav-link" href="#">설정</a>
            </li>
        </ul>
    </div>
</nav>

<div class="container mt-4">
    <div class="row">
        <div class="col-md-3">
            <div class="list-group">
                <a href="#" class="list-group-item list-group-item-action active" data-target="#dashboard">대시보드</a>
                <a href="#" class="list-group-item list-group-item-action" data-target="#user-management">사용자 관리</a>
                <a href="#" class="list-group-item list-group-item-action" data-target="#file-management">파일 관리</a>
                <a href="#" class="list-group-item list-group-item-action" data-target="#storage-usage">스토리지 사용량</a>
                <a href="#" class="list-group-item list-group-item-action" data-target="#settings">설정</a>
            </div>
        </div>
        <div class="col-md-9">
            <div id="dashboard">
                <h1>대시보드</h1>
                <p>여기에 대시보드 내용을 추가하세요.</p>
            </div>

            <div id="user-management" class="mt-4" style="display: none;">
                <h2>사용자 관리</h2>
                <table class="table table-bordered">
                    <thead>
                    <tr>
                        <th>사용자 ID</th>
                        <th>이름</th>
                        <th>이메일</th>
                        <th>가입일</th>
                        <th>작업</th>
                    </tr>
                    </thead>
                    <tbody>
                    <!-- 추가 사용자 데이터 -->
                    </tbody>
                </table>
            </div>

            <div id="file-management" class="mt-4" style="display: none;">
                <h2>파일 관리</h2>
                <table class="table table-bordered">
                    <thead>
                    <tr>
                        <th>파일 ID</th>
                        <th>파일 이름</th>
                        <th>파일 크기</th>
                        <th>업로드 날짜</th>
                        <th>작업</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>1</td>
                        <td>example.txt</td>
                        <td>15 KB</td>
                        <td>2024-01-01</td>
                        <td>
                            <button class="btn btn-primary btn-sm">다운로드</button>
                            <button class="btn btn-danger btn-sm">삭제</button>
                        </td>
                    </tr>
                    <!-- 추가 파일 데이터 -->
                    </tbody>
                </table>
            </div>

            <div id="storage-usage" class="mt-4" style="display: none;">
                <h2>스토리지 사용량</h2>
                <p>여기에 스토리지 사용량 통계를 추가하세요.</p>
                <div id="storageChart" class="chart-container"></div>
            </div>

            <div id="settings" class="mt-4" style="display: none;">
                <h2>설정</h2>
                <p>여기에 설정 내용을 추가하세요.</p>
            </div>
        </div>
    </div>
</div>

<!-- Modal for Editing User -->
<div class="modal fade" id="editUserModal" tabindex="-1" role="dialog" aria-labelledby="editUserModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="editUserModalLabel">사용자 편집</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="editUserForm">
                    <div class="form-group">
                        <label for="editUserName">이름</label>
                        <input type="text" class="form-control" id="editUserName" required>
                    </div>
                    <div class="form-group">
                        <label for="editUserEmail">이메일</label>
                        <input type="email" class="form-control" id="editUserEmail" required>
                    </div>
                    <div class="form-group">
                        <label for="editUserPassword">패스워드</label>
                        <input type="text" class="form-control" id="editUserPassword" required>
                    </div>
                    <div class="form-group">
                        <label for="editUserLevel">레벨</label>
                        <input type="number" class="form-control" id="editUserLevel" min="1" max="2" required>
                    </div>
                    <div class="form-group">
                        <label for="editUserStorageMaxSize">스토리지 최대 크기</label>
                        <input type="number" class="form-control" id="editUserStorageMaxSize" required>
                    </div>
                    <input type="number" id="editUserId" hidden>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">닫기</button>
                <button type="button" class="btn btn-primary" id="saveEditUser">저장</button>
            </div>
        </div>
    </div>
</div>

<script src="<c:url value="/js/admin.js"/>"></script>
</body>
</html>
