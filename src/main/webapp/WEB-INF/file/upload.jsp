<%--<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>upload</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js"
            integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <link rel="stylesheet" href="<c:url value="/css/upload.scss?ver=1"/>">
</head>
<body>
<div>
    <div class="head1">
        <div class="logo">
        </div>
        <div class="history"> <%--기록 페이지로 이동--%>
            <a href=""></a>
        </div>
    </div>

    <div class="body1">
        <div class="show">
            <div class="test-view">
                <div class="path">now path: ${storagePath}</div>
                removeUserIdPath: ${removeUserIdPath}
                parentFolderID: ${parentFolderID}
            </div>
            <div class="file-list-container drag-drop-area">
                <table class="file-list-table">
                    <tr>
                        <th>filename</th>
                        <th>UploadDate</th>
                        <th>LastModifiedDate</th>
                        <th>download</th>
                    </tr>
                    <c:if test="${removeUserIdPath ne 'root'}">
                        <tr>
                            <td colspan="4" class="folder-area" onclick="enterFolder(${parentFolderID})">...</td>
                        </tr>
                    </c:if>
                    <c:forEach var="folder" items="${subFolderList}">
                        <tr>
                            <td colspan="4" class="folder-area"
                                onclick="enterFolder(${folder.folderID})">${folder.folderName}</td>
                        </tr>
                    </c:forEach>
                    <c:forEach var="file" items="${fileList}">
                        <tr>
                            <td>${file.filename}</td>
                            <td>${file.uploadDate}</td>
                            <td>${file.lastModifiedDate}</td>
                            <td>
                                <div class="download-btn"
                                     onclick="downLoadFile(${userID}, ${file.fileID}, '${file.filename}')">download
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
        <div class="enter">
            <form action="upload" method="post" enctype="multipart/form-data"
                  onsubmit="return fileUploadHandler(event)">
                <div class="add"> <%--파일, 디렉토리 추가--%>
                    <div class="add_file">
                        <input type="file" name="file" required/>
                        <input type="text" name="userID" value="${userID}" readonly>
                        <input type="text" name="storagePath" value="${storagePath}" readonly>
                        <input type="number" name="folderID" value="${p}" hidden readonly>
                    </div>
                    <div class="add_folder">
                        <input type="text" id="addFolderName" name="addFolderName" placeholder="addFolderName">
                        <button type="button" id="addFolder"
                                onclick='addFolderHandler(${userID}, ${p}, "${storagePathJS}")'>add folder
                        </button>
                    </div>
                </div>
                <div class="upload"> <%--업로드--%>
                    <input type="submit" value="Upload"/>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal -->
    <div class="modal fade" id="uploadModal" tabindex="-1" aria-labelledby="uploadModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="uploadModalLabel">Uploading...</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="progress mb-3">
                        <div id="progressBar" class="progress-bar" role="progressbar" style="width: 0%;"
                             aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
                    </div>
                    <div>
                        <p id="uploadDetails"></p>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>
<script src="<c:url value="/js/upload.js?ver=1"/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
