<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>upload</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js"
            integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <script src="https://vjs.zencdn.net/7.11.4/video.min.js"></script>

    <link href="https://vjs.zencdn.net/7.11.4/video-js.css" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value="/css/upload.css?ver=1"/>">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
<jsp:include page="../header.jsp"/>
<jsp:include page="../sidebar.jsp"/>
<div>
    <div class="body1">
        <div class="show">
            <div class="test-view">
                <div class="path">now path: <span id="currentPath"></span></div>
                removeUserIdPath: <span id="removeUserIdPath"></span>
                parentFolderID: <span id="parentFolderID"></span>
            </div>
            <div class="file-list-container drag-drop-area">
                <table class="file-list-table">
                    <tbody></tbody>
                </table>
            </div>
        </div>
        <div class="enter">
            <%--            <form id="uploadForm" action="upload" method="post" enctype="multipart/form-data"--%>
            <%--                  onsubmit="return fileUploadHandler(event)">--%>
            <%--                <div class="add"> &lt;%&ndash;파일, 디렉토리 추가&ndash;%&gt;--%>
            <%--                    <div class="add_file">--%>
            <%--                        <input type="file" name="file" id="file" required/> &lt;%&ndash; 버튼속성 &ndash;%&gt;--%>
            <%--                        <input type="text" name="userID" value="" id="userID" readonly>--%>
            <%--                        <input type="text" name="storagePath" value="" id="storagePath" readonly>--%>
            <%--                        <input type="number" name="folderID" value="" id="folderID" hidden readonly>--%>
            <%--                    </div>--%>
            <%--                    <div class="add_folder">--%>
            <%--                        <input type="text" id="addFolderName" name="addFolderName" placeholder="addFolderName">--%>
            <%--                        <button type="button" id="addFolder" onclick='addFolderHandler(0, 0, "")'>add folder</button>--%>
            <%--                    </div>--%>
            <%--                </div>--%>
            <%--                <div class="upload"> &lt;%&ndash;업로드&ndash;%&gt;--%>
            <%--                    <input type="submit" value="Upload"/>--%>
            <%--                </div>--%>

            <%--            </form>--%>

            <form id="uploadForm" action="upload" method="post" enctype="multipart/form-data"
                  onsubmit="return fileUploadHandler(event)">
                <div class="add"> <%--파일, 디렉토리 추가--%>
                    <div class="add_file">
                        <div class="filebox">
                            <input class="upload-name" value="addFilename" placeholder="addFilename">
                            <label for="file">Add File</label>
                            <input type="file" name="file" id="file" required>
                        </div>
                        <input type="text" name="userID" value="" id="userID" readonly>
                        <input type="text" name="storagePath" value="" id="storagePath" readonly>
                        <input type="number" name="folderID" value="" id="folderID" hidden readonly>
                    </div>
                    <div class="add_folder">
                        <input type="text" id="addFolderName" name="addFolderName" placeholder="addFolderName">
                        <button type="button" id="addFolder" onclick='addFolderHandler(0, 0, "")'>Add Directory</button>
                    </div>
                </div>
                <div class="upload"> <%--업로드--%>
                    <input type="submit" id="uploadbtn" value="Upload"/>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal -->
    <div class="modal fade" id="fileModal" tabindex="-1" aria-labelledby="fileModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="fileModalLabel">Processing...</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div id="videoContainer" class="d-none">
                        <video id="videoPlayer" class="video-js vjs-default-skin" controls preload="auto">
                            <source id="videoSource" src="" type="video/mp4">
                        </video>
                    </div>
                    <div id="fileDetails">
                        <!-- File details will be dynamically inserted here -->
                    </div>
                    <div class="progress mb-3">
                        <div id="progressBar" class="progress-bar" role="progressbar" style="width: 0%;"
                             aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
                    </div>
                    <button type="button" class="btn btn-primary d-none" id="downloadBtn" onclick="" hidden>Download
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Share Modal -->
    <div class="modal fade" id="shareModal" tabindex="-1" aria-labelledby="shareModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="shareModalLabel">파일 공유</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="shareForm">
                        <div class="form-group">
                            <label for="sharePassword">비밀번호 (선택 사항)</label>
                            <input type="password" class="form-control" id="sharePassword" placeholder="비밀번호를 입력하세요">
                        </div>
                        <div class="form-group">
                            <label for="shareExpiration">공유 기한</label>
                            <input type="date" class="form-control" id="shareExpiration" required>
                        </div>
                        <button type="submit" class="btn btn-primary" id="shareSubmit">공유 시작</button>
                    </form>
                    <div id="shareLinkContainer" class="mt-3" style="display: none;">
                        <h5>공유 링크:</h5>
                        <a href=""><p id="shareLink"></p></a>
                        <button type="button" class="btn btn-danger" id="stopShareBtn" style="display: none;">공유 중지
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- clicked -->
    <div id="clicked"> <!--우클릭 시-->
        <button type="button" class="btn btn-primary" id="fakeDownloadBtn" onclick="">Download</button>
        <button type="button" class="btn btn-primary" id="DeleteBtn" onclick="">Delete</button>
        <button type="button" class="btn btn-primary" id="ShareBtn" onclick="">Share</button>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value="/js/upload.js?ver=1"/>"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const initialFolderID = ${p}; // 초기 폴더 ID를 설정
        $('#folderID').val(initialFolderID);
        enterFolder(initialFolderID);
    });

</script>
</body>
</html>
