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
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.1/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
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
        <ul class="navbar-nav ml-auto">
            <li class="nav-item">
                <a class="logout-link" href="">Log out</a>
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
                <%--                <h1>대시보드</h1>--%>
                <div id="serverStatus">
                    <h2>Server Status</h2>
                    <p>Status: <span id="serverStatusText"></span></p>
                    <p>Uptime: <span id="serverUptime"></span></p>
                </div>
                    <hr>
                <div id="databaseStatus" class="mt-4">
                    <h2>Database Status</h2>
                    <p>Status: <span id="databaseStatusText"></span></p>
                    <p>Database Size: <span id="databaseSize"></span></p>
                </div>
                    <hr>
                <div id="storageUsage" class="mt-4">
                    <h2>Storage Usage</h2>
                    <p>Used Space: <span id="usedSpace"></span></p>
                    <p>Total Space: <span id="totalSpace"></span></p>
                </div>
                    <hr>
                <div id="realTimeActivity" class="mt-4">
                    <h2>실시간 파일 활동</h2>
                    <ul id="activityLog" class="list-group"></ul>
                </div>
                    <hr>
                <h2>로그 파일</h2>
                <ul id="logFiles" class="list-group">
                </ul>
                <div id="logContent" style="display: none; margin-top: 20px;">
                    <h3>로그 내용</h3>
                    <pre id="logText" style="height: 60vh; overflow-y: scroll; white-space: pre-wrap"></pre>
                </div>
            </div>

            <div id="user-management" class="mt-4" style="display: none;">
                <h2>사용자 관리</h2>
                <br>
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
                <h2>파일 검색</h2>
                <br>
                <form id="searchForm">
                    <div class="row mb-3">
                        <div class="col-md-3" id="col-md-3-1">
                            <input type="text" id="filename" name="filename" class="form-control" placeholder="파일 이름">
                        </div>
                        <div class="col-md-3" id="col-md-3-2">
                            <input type="date" id="startDate" name="startDate" class="form-control" placeholder="시작 날짜">
                        </div>
                        <span class="tilde">~</span>
                        <div class="col-md-3" id="col-md-3-3">
                            <input type="date" id="endDate" name="endDate" class="form-control" placeholder="종료 날짜">
                        </div>
                        <div class="col-md-3" id="col-md-3-4">
                            <input type="number" id="minFileSize" name="minFileSize" class="form-control"
                                   placeholder="최소 파일 크기">
                            <select id="minFileSizeUnit" name="minFileSizeUnit" class="form-control">
                                <option value="B">B</option>
                                <option value="KB">KB</option>
                                <option value="MB">MB</option>
                                <option value="GB">GB</option>
                            </select>
                        </div>
                        <span class="tilde">~</span>
                        <div class="col-md-3" id="col-md-3-5">
                            <input type="number" id="maxFileSize" name="maxFileSize" class="form-control"
                                   placeholder="최대 파일 크기">
                            <select id="maxFileSizeUnit" name="maxFileSizeUnit" class="form-control">
                                <option value="B">B</option>
                                <option value="KB">KB</option>
                                <option value="MB">MB</option>
                                <option value="GB">GB</option>
                            </select>
                        </div>
                        <div class="col-md-3" id="col-md-3-6">
                            <input type="text" id="userId" name="userId" class="form-control" placeholder="사용자 ID">
                        </div>
                        <button class="btn btn-primary mb-3" id="search-button" type="submit">검색</button>
                    </div>

                </form>
                <div id="searchResults">
                    <!-- 검색 결과 표시 -->
                </div>
            </div>

            <div id="storage-usage" class="mt-4" style="display: none;">
                <h2>스토리지 사용량</h2>
                <br>
                <div id="storageChart" class="chart-container"></div>
            </div>

            <div id="settings" class="mt-4" style="display: none;">
                <h2>설정</h2>
                <br>
                <button class="btn btn-primary" id="backupButton">백업</button>
                <input type="file" id="restoreFileInput" style="display: none;">
                <button class="btn btn-secondary" id="restoreButton">복원</button>
                <br><br>
                <hr>
                <br><br>
                <h3>공지사항 업로드</h3> <br>
                <form id="noticeForm">
                    <div class="form-group">
                        <label for="noticeTitle">제목</label>
                        <input type="text" class="form-control" id="noticeTitle" name="title" required>
                    </div>
                    <div class="form-group">
                        <label for="noticeContent">내용</label>
                        <textarea class="form-control" id="noticeContent" name="content" rows="5" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">업로드</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Modal for Editing User -->
<div class="modal fade" id="editUserModal" tabindex="-1" role="dialog" aria-labelledby="editUserModalLabel"
     aria-hidden="true">
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous">
</script>

<%--
TODO
 클라우드 스토리지 프로젝트의 관리자 페이지는 여러 가지 중요한 기능을 포함할 수 있습니다. 이미 사용자 관리와 스토리지 사용량 기능을 구현하셨다면, 나머지 항목에 대해서도 구체적으로 어떤 기능을 추가하면 좋을지 설명드리겠습니다. 필요 없을 수 있는 항목이나 추가하면 좋을 항목도 함께 제안드리겠습니다.
 1. 대시보드
 필수 항목
 사용자 활동: 최근 로그인한 사용자, 가장 많이 사용하는 사용자, 최근 업로드된 파일.
 파일 업로드/다운로드 통계: 최근 24시간, 7일, 30일 동안의 파일 업로드 및 다운로드 통계.
 알림 및 공지: 시스템 알림, 유지보수 공지, 업데이트 정보 등.
 추가하면 좋은 기능
 비즈니스 인텔리전스: 사용자 및 파일 사용 패턴에 대한 분석.
 2. 파일 관리
 필수 항목
 파일 미리보기 및 다운로드: 선택한 파일의 미리보기 기능과 다운로드 기능.
 파일 삭제 및 복원: 파일 삭제와 휴지통에서 복원 기능.
 파일 접근 권한 관리: 파일별로 접근 권한 설정(공개, 비공개, 특정 사용자만 접근 가능).
 추가하면 좋은 기능
 버전 관리: 파일의 여러 버전을 관리하고, 이전 버전으로 복원할 수 있는 기능.
 파일 태그 및 메타데이터 관리: 파일에 태그를 추가하고 메타데이터를 관리할 수 있는 기능.
 3. 설정
 필수 항목
 일반 설정: 시스템 이름, 로고, 기본 언어, 시간대 설정.
 보안 설정: 비밀번호 정책, 2단계 인증 설정, 세션 타임아웃 설정.
 이메일 설정: 시스템 알림 및 공지를 위한 이메일 서버 설정.
 추가하면 좋은 기능
 API 관리: API 키 생성 및 관리, API 사용 통계.
 통합 및 연동: 외부 서비스(Google Drive, Dropbox 등)와의 통합 설정.
 4. 추가하면 좋을 항목
 보고서 생성
 사용자 보고서: 특정 기간 동안의 사용자 활동 보고서.
 스토리지 보고서: 스토리지 사용량, 파일 업로드/다운로드 통계 보고서.
 시스템 유지보수
 시스템 업데이트: 시스템 및 소프트웨어 업데이트 관리.
 디스크 정리: 사용하지 않는 파일 삭제, 시스템 최적화.
 이와 같은 기능을 통해 관리자는 클라우드 스토리지 시스템을 효율적으로 관리할 수 있으며, 사용자 경험도 향상시킬 수 있습니다. 각 항목을 구현할 때, 관리자 페이지의 유용성과 보안성을 고려하여 기능을 추가하는 것이 중요합니다.
--%>