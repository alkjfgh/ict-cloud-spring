<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>파일 다운로드</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
</head>
<body>
<div class="container mt-5">
    <div class="row">
        <div class="col-md-12">
            <h2 class="text-center">파일 다운로드</h2>
            <div id="fileInfo" style="display: none;">
                <h4>파일 정보</h4>
                <p id="fileName">파일 이름: </p>
                <p id="fileSize">파일 크기: </p>
                <form id="downloadForm" class="mt-4">
                    <button type="submit" class="btn btn-primary btn-block">다운로드</button>
                </form>
                <div id="downloadError" class="alert alert-danger mt-4" style="display: none;">
                    다운로드 실패: 접근 권한이 없거나 파일이 만료되었습니다.
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Password Modal -->
<div class="modal fade" id="passwordModal" tabindex="-1" role="dialog" aria-labelledby="passwordModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="passwordModalLabel">비밀번호 입력</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="passwordForm">
                    <div class="form-group">
                        <label for="password">비밀번호</label>
                        <input type="password" class="form-control" id="password" placeholder="비밀번호를 입력하세요">
                    </div>
                    <button type="submit" class="btn btn-primary">확인</button>
                </form>
                <div id="passwordError" class="alert alert-danger mt-4" style="display: none;">
                    비밀번호가 틀렸습니다.
                </div>
            </div>
        </div>
    </div>
</div>

<script src="<c:url value="/js/share.js"/>"></script>
</body>
</html>
