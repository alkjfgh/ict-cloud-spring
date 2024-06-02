document.addEventListener("DOMContentLoaded", function () {
    const dragDropArea = document.getElementsByClassName('drag-drop-area')[0];

    dragDropArea.addEventListener('dragover', function (e) {
        e.preventDefault(); // 기본 이벤트 방지
        e.stopPropagation(); // 이벤트 전파 방지
        e.dataTransfer.dropEffect = 'copy';
        dragDropArea.style.backgroundColor = 'rgb(72, 74, 83)'
    });

    dragDropArea.addEventListener('dragleave', (event) => {
        dragDropArea.style.backgroundColor = '';
    });

    dragDropArea.addEventListener('drop', function (e) {
        e.preventDefault();
        e.stopPropagation();
        dragDropArea.style.backgroundColor = '';

        const files = e.dataTransfer.files;
        if (files.length > 0) {
            const formData = new FormData();
            for (let i = 0; i < files.length; i++) {
                formData.append('file', files[i]); // 파일 추가
            }

            // userID, storagePath, folderID 추가
            const userID = document.querySelector('input[name="userID"]').value;
            const storagePath = document.querySelector('input[name="storagePath"]').value;
            const folderID = document.querySelector('input[name="folderID"]').value;

            formData.append('userID', userID);
            formData.append('storagePath', storagePath);
            formData.append('folderID', folderID);

            fileUploadHandler(new Event('submit', {cancelable: true, bubbles: true}), formData).then(r => {
            });
        }
    });
});


$('#fakeDownloadBtn').on('click', async function () {
    const fakeDownloadBtn = document.getElementById('fakeDownloadBtn');

    const userid = fakeDownloadBtn.dataset.userid;
    const fileid = fakeDownloadBtn.dataset.fileid;
    const filename = fakeDownloadBtn.dataset.filename;
    const filesize = parseInt(fakeDownloadBtn.dataset.filesize);
    const filetype = fakeDownloadBtn.dataset.filetype;

    await showFileDetails(userid, fileid, filename, filesize, filetype);

    $('#downloadBtn').click();
});

$('#DeleteBtn').on('click', async function () {
    const DeleteBtn = document.getElementById('DeleteBtn');

    const userid = DeleteBtn.dataset.userid;
    const fileid = DeleteBtn.dataset.fileid;
    const folderid = DeleteBtn.dataset.folderid;

    $('#clicked').hide();

    if (folderid === 'null') {
        await deleteFileDetail(userid, fileid);
    } else if (fileid === 'null') {
        await folderDeleteHandler(userid, folderid);
    }

});

$('#ShareBtn').on('click', function () {
    $('#clicked').hide();
    const shareBtn = document.getElementById('ShareBtn');

    const userid = shareBtn.dataset.userid;
    const id = shareBtn.dataset.id;
    const type = shareBtn.dataset.type;

    axios.post('/share/existing', {ownerId: userid, itemId: id, itemType: type})
        .then(function (response) {
            if (response.status === 200) {
                const shareId = response.data.shareID;
                $('#shareLink').text(window.location.origin + '/share/' + shareId);
                $('#shareLink').attr('href', window.location.origin + '/share/' + shareId);
                $('#shareLink').attr('target', '_blank');
                $('#shareLinkContainer').show();
                $('#shareSubmit').hide();
                $('#stopShareBtn').show();
                $('#shareForm').hide();
                $('#shareModal').modal('show');
            } else if (response.status === 201) {
                $('#shareLinkContainer').hide();
                $('#shareForm').show();
                $('#shareSubmit').show();
                $('#stopShareBtn').hide();
                $('#shareModal').modal('show');
            }
        })
        .catch(function (error) {
            console.error('Error checking existing share:', error);
        });
});

$('#stopShareBtn').click(function () {
    const shareId = $('#shareLink').text().split('/').pop();

    axios.delete('/share/stop', {params: {shareId: shareId}})
        .then(function (response) {
            if (response.status === 200) {
                alert('Share link stopped successfully.');
                $('#shareLinkContainer').hide();
                $('#shareSubmit').show();
                $('#stopShareBtn').hide();
                $('#shareModal').modal('hide');
            } else {
                alert('Failed to stop share link.');
            }
        })
        .catch(function (error) {
            console.error('Error stopping share link:', error);
        });
});

$("#file").on('change', function () {
    const fileName = $("#file").val();
    $(".upload-name").val(fileName);
});

$('#shareForm').submit(function (e) {
    e.preventDefault();

    const shareBtn = document.getElementById('ShareBtn');

    const userid = shareBtn.dataset.userid;
    const id = shareBtn.dataset.id;
    const type = shareBtn.dataset.type;
    const password = $('#sharePassword').val();
    const expirationDate = $('#shareExpiration').val();

    axios.post('/share/create', {
        ownerID: userid,
        itemID: id,
        itemType: type,
        permissionType: password ? 'protected' : 'open',
        sharePassword: password,
        expirationDate: expirationDate
    }).then(function (response) {
        if (response.status === 200) {
            const shareId = response.data;
            $('#shareLink').text(window.location.origin + '/share/' + shareId);
            $('#shareLink').attr('href', window.location.origin + '/share/' + shareId);
            $('#shareLink').attr('target', '_blank');
            $('#shareLinkContainer').show();
            $('#shareSubmit').hide();
            $('#stopShareBtn').show();
            $('#shareForm').hide();
        } else {
            alert('Failed to create share link.');
        }
    })
        .catch(function (error) {
            console.error('Error creating share link:', error);
        });
});

document.addEventListener('DOMContentLoaded', () => { //오른쪽 클릭 시 다운로드, 삭제기능 창 뜨기
    const clickdiv = document.getElementById('clicked');
    const target = document.getElementsByClassName('file-list-table')[0];
    const dbtn = document.getElementById('fakeDownloadBtn');

    document.addEventListener('contextmenu', (event) => {
        event.preventDefault();

        const clickX = event.clientX;
        const clickY = event.clientY;


        if (event.target.tagName === 'TD' && target.contains(event.target)) {
            dbtn.style.display = 'inline';
            const trElement = event.target.closest('tr');
            const userID = trElement ? trElement.getAttribute('data-user-id') : null;
            const fileID = trElement ? trElement.getAttribute('data-id') : null;
            const filename = trElement ? trElement.getAttribute('data-file-name') : null;
            const fileSize = trElement ? trElement.getAttribute('data-file-size') : null;
            const fileType = trElement ? trElement.getAttribute('data-file-type') : null;
            const folderID = trElement ? trElement.getAttribute('data-folder-id') : null;

            clickdiv.style.left = `${clickX}px`;
            clickdiv.style.top = `${clickY}px`;
            clickdiv.style.display = 'block';

            document.getElementById('fakeDownloadBtn').dataset.userid = userID;
            document.getElementById('fakeDownloadBtn').dataset.fileid = fileID;
            document.getElementById('fakeDownloadBtn').dataset.filename = filename;
            document.getElementById('fakeDownloadBtn').dataset.filesize = fileSize;
            document.getElementById('fakeDownloadBtn').dataset.filetype = fileType;

            document.getElementById('DeleteBtn').dataset.userid = userID;
            document.getElementById('DeleteBtn').dataset.fileid = fileID;
            document.getElementById('DeleteBtn').dataset.folderid = folderID;

            document.getElementById('ShareBtn').dataset.userid = userID;
            document.getElementById('ShareBtn').dataset.id = fileID ? fileID : folderID;
            document.getElementById('ShareBtn').dataset.type = fileID ? 'file' : 'folder';

            if (event.target.className === 'folder-area') { //타겟이 폴더
                dbtn.style.display = 'none';
            }
        } else {
            clickdiv.style.display = 'none';
        }
    });
    document.addEventListener('click', (event) => {  //클릭하면 div사라지기
        if (event.target.id !== 'downloadBtn' && event.target.id !== 'clicked' && !document.getElementById('clicked').contains(event.target)) {
            clickdiv.style.display = 'none';
        }
    });
});


const showModal = () => {
    const modal = new bootstrap.Modal(document.getElementById('fileModal'));
    modal.show();
};

const hideModal = () => {
    const modal = new bootstrap.Modal(document.getElementById('fileModal'));
    modal.hide();
};

const resetModal = () => {
    const progressBar = document.getElementById('progressBar');
    const fileDetails = document.getElementById('fileDetails');
    const downloadBtn = document.getElementById('downloadBtn');


    progressBar.style.width = '0%';
    progressBar.setAttribute('aria-valuenow', 0);
    progressBar.innerText = '0%';

    fileDetails.innerText = '';

    downloadBtn.classList.add('d-none');
    downloadBtn.onclick = null;

};

const formatSize = (size) => {
    if (size === 'N/A') {
        return size;
    }
    if (size >= 1024 * 1024 * 1024) {
        return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
    }
    if (size >= 1024 * 1024) {
        return (size / (1024 * 1024)).toFixed(2) + ' MB';
    }
    if (size >= 1024) {
        return (size / 1024).toFixed(2) + ' KB';
    }
    return size.toFixed(2) + ' bytes';
};

const formatDate = (dateStr) => {
    const date = new Date(dateStr);

    date.setHours(date.getHours() + 9);

// 한국 표준 시간대 (KST)로 변환하여 포맷
    const options = {
        timeZone: 'Asia/Seoul',
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    };

    const formatter = new Intl.DateTimeFormat('ko-KR', options);
    const formattedDate = formatter.format(date);

    return formattedDate;
}

const updateProgress = (progressBarId, fileDetailsId, startTime, loaded, total) => {
    const progressBar = document.getElementById(progressBarId);
    const fileDetails = document.getElementById(fileDetailsId);

    const percentCompleted = total ? Math.round((loaded * 100) / total) : 0;
    const elapsedTime = (new Date().getTime() - startTime) / 1000; // seconds
    const speed = formatSize(loaded / elapsedTime); // Bytes/s
    const totalSize = total ? formatSize(total) : 'N/A';
    const loadedSize = formatSize(loaded);
    const remainingSize = total ? formatSize(total - loaded) : 'N/A';

    // 예상 남은 시간 계산
    const remainingTime = total ? ((total - loaded) / (loaded / elapsedTime)) : 0;
    const remainingHours = Math.floor(remainingTime / 3600);
    const remainingMinutes = Math.floor((remainingTime % 3600) / 60);
    const remainingSeconds = Math.floor(remainingTime % 60);

    // 소요된 시간 계산
    const elapsedHours = Math.floor(elapsedTime / 3600);
    const elapsedMinutes = Math.floor((elapsedTime % 3600) / 60);
    const elapsedSeconds = Math.floor(elapsedTime % 60);

    progressBar.style.width = percentCompleted + '%';
    progressBar.setAttribute('aria-valuenow', percentCompleted);
    progressBar.innerText = percentCompleted + '%';

    fileDetails.innerText = `
        Speed: ${speed} /s
        Loaded: ${loadedSize} / ${totalSize}
        Remaining: ${remainingSize}
        Elapsed Time: ${elapsedHours}h ${elapsedMinutes}m ${elapsedSeconds}s
        Estimated Remaining Time: ${remainingHours}h ${remainingMinutes}m ${remainingSeconds}s
    `;
};

const fileUploadHandler = async (event, formData = null) => {
    event.preventDefault(); // 폼의 기본 제출 동작 방지

    // formData가 직접 전달되지 않은 경우, 폼에서 FormData 객체를 생성
    if (!formData) {
        const form = event.target;
        formData = new FormData(form);
    }
    const file = formData.get('file');
    const filename = file.name;
    const folderID = formData.get('folderID');

    // 현재 폴더의 파일 목록을 가져와서 중복된 이름이 있는지 확인
    try {
        const response = await fetch(`/file/upload?p=${folderID}`, {
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        if (response.status === 200) {
            const data = await response.json();
            const fileList = data.fileList;
            const isDuplicate = fileList.some(existingFile => existingFile.filename === filename);

            if (isDuplicate) {
                alert("같은 이름의 파일이 이미 존재합니다.");
                return;
            }
        } else {
            alert("파일 목록을 불러오는 데 실패했습니다.");
            return;
        }
    } catch (error) {
        console.error("Error fetching file list:", error);
        alert("파일 목록을 불러오는 중 오류가 발생했습니다.");
        return;
    }

    const progressBarId = 'progressBar';
    const fileDetailsId = 'fileDetails';
    const downloadBtn = document.getElementById('downloadBtn');
    downloadBtn.classList.add('d-none');
    showModal();

    let startTime = new Date().getTime();

    try {
        const response = await axios.post('/file/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            onUploadProgress: (progressEvent) => {
                updateProgress(progressBarId, fileDetailsId, startTime, progressEvent.loaded, progressEvent.total);
            }
        });

        if (response.status === 200) {
            // 성공적으로 업로드 완료 시 처리
            // alert("File uploaded successfully");
            await enterFolder(formData.get("folderID"));
        } else {
            // 업로드 실패 시 처리
            alert("File upload failed");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("An error occurred during the file upload");
    } finally {
        // resetModal();
        // hideModal();
    }
};

const fileDownloadHandler = async (userID, fileID, filename, fileSize) => {
    console.log('download file');

    const progressBarId = 'progressBar';
    const fileDetailsId = 'fileDetails';
    const downloadBtn = document.getElementById('downloadBtn');
    downloadBtn.classList.add('d-none');
    showModal();

    let startTime = new Date().getTime();

    try {
        const response = await axios({
            url: `/file/download?userID=${encodeURIComponent(userID)}&fileID=${encodeURIComponent(fileID)}&filename=${encodeURIComponent(filename)}`,
            method: 'GET',
            responseType: 'blob',
            timeout: 600000, // 10 minutes
            onDownloadProgress: (progressEvent) => {
                updateProgress(progressBarId, fileDetailsId, startTime, progressEvent.loaded, fileSize /*progressEvent.total*/);
            }
        });

        if (response.status === 200) {
            const blob = new Blob([response.data], {type: 'application/octet-stream'});
            const downloadUrl = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = downloadUrl;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(downloadUrl);
            document.body.removeChild(a);
        } else {
            alert("파일을 다운로드하는 데 실패했습니다.");
        }
    } catch (error) {
        console.error('Error:', error);
        alert('파일을 다운로드하는 중 오류가 발생했습니다.');
    } finally {
        // resetModal();
        // hideModal();
    }
};

const addFolderHandler = async (userID, folderID, storagePath) => {
    const newFolderName = $("#addFolderName").val();

    if (newFolderName.length === 0) {
        alert("Foldername cannot be empty");
        return false;
    }

    // 현재 폴더의 폴더 목록을 가져와서 중복된 이름이 있는지 확인
    try {
        const response = await fetch(`/file/upload?p=${folderID}`, {
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        if (response.status === 200) {
            const data = await response.json();
            const folderList = data.subFolderList;
            const isDuplicate = folderList.some(existingFolder => existingFolder.folderName === newFolderName);

            if (isDuplicate) {
                alert("같은 이름의 폴더가 이미 존재합니다.");
                return false;
            }
        } else {
            alert("폴더 목록을 불러오는 데 실패했습니다.");
            return false;
        }
    } catch (error) {
        console.error("Error fetching folder list:", error);
        alert("폴더 목록을 불러오는 중 오류가 발생했습니다.");
        return false;
    }

    // 중복된 이름이 없는 경우에만 폴더 생성 요청
    await fetch("/file/addFolder", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            userID: userID,
            storagePath: storagePath,
            folderID: folderID,
            addFolderName: newFolderName
        }),
    }).then((response) => {
        if (response.status === 200) {
            enterFolder(folderID);
        } else {
            alert("폴더 생성에 실패했습니다.");
        }
    });
}

const enterFolder = async (p) => {
    history.pushState({p: p}, "", "/file/upload?p=" + p);

    const response = await fetch(`/file/upload?p=${p}`, {
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        }
    });

    if (response.ok) {
        const data = await response.json();

        const testView = document.getElementsByClassName('test-view')[0];
        testView.innerHTML = '<div class="path">now path: ' + data.storagePath + '</div>\n' +
            '                removeUserIdPath: ' + data.removeUserIdPath + '\n' +
            '                parentFolderID: ' + data.parentFolderID + '';

        const addFile = document.getElementsByClassName('add_file')[0];
        addFile.children.item(1).value = data.userID;
        addFile.children.item(2).value = data.storagePath;
        addFile.children.item(3).value = data.p;

        const addFolder = document.getElementById('addFolder');
        addFolder.onclick = () => addFolderHandler(data.userID, data.p, data.storagePathJS);

        const fileListElement = document.getElementsByClassName('file-list-table')[0];
        fileListElement.innerHTML = '<tr>\n' +
            '                        <th>filename</th>\n' +
            '                        <th>UploadDate</th>\n' +
            '                        <th>LastModifiedDate</th>\n' +
            '                        <th>filesize</th>\n' +
            '                        <th>filetype</th>\n' +
            '                    </tr>';

        if (data.removeUserIdPath !== 'root') {
            fileListElement.innerHTML += '<tr>\n' +
                '                            <td colspan="5" class="folder-area" onclick="enterFolder(' + data.parentFolderID + ')">...</td>\n' +
                '                        </tr>';
        }

        const folderList = updateFolderList(data.subFolderList);
        folderList.forEach(element => {
            // $('.file-list-table').find("tbody").append(element);
            fileListElement.appendChild(element)
        });

        const fileList = updateFileList(data.fileList, data.userID);
        fileList.forEach(element => {
            // $('.file-list-table').find("tbody").append(element);
            fileListElement.appendChild(element)
        });

        // fileListElement.load(location.href + ' .file-list-table');
    } else {
        alert("Failed to load folder data.");
    }
}

const updateFolderList = (folderList) => {
    let tmpList = [];

    folderList.forEach(folder => {
        let folderElement = document.createElement("tr");
        let folderInner = document.createElement("td");

        folderElement.setAttribute('data-user-id', folder.userID);
        folderElement.setAttribute('data-folder-id', folder.folderID);

        folderInner.className = "folder-area";
        folderInner.colSpan = 5;
        folderInner.onclick = () => enterFolder(folder.folderID);
        folderInner.innerHTML = folder.folderName;

        folderElement.appendChild(folderInner);
        // folderElement.dataset.id = folder.userID;
        // folderElement.dataset.id = folder.folderID;

        tmpList.push((folderElement));
    });

    return tmpList;
}

const updateFileList = (fileList, userID) => {
    let tmpList = [];

    fileList.forEach(file => {
        let fileElement = document.createElement("tr");
        let fileInner1 = document.createElement("td");
        let fileInner2 = document.createElement("td");
        let fileInner3 = document.createElement("td");
        let fileInner4 = document.createElement("td");
        let fileInner5 = document.createElement("td");

        let fileTypeClass = getFileTypeClass(file.fileType);

        fileElement.className = `file-area ${fileTypeClass}`;
        fileElement.setAttribute('data-file-id', file.fileID);
        fileElement.setAttribute('data-user-id', userID);
        fileElement.setAttribute('data-file-name', file.filename);
        fileElement.setAttribute('data-file-size', file.fileSize);
        fileElement.setAttribute('data-file-type', file.fileType);

        fileInner1.innerHTML = file.filename;
        fileInner2.innerHTML = formatDate(file.uploadDate);
        fileInner3.innerHTML = formatDate(file.lastModifiedDate);
        fileInner4.innerHTML = formatSize(file.fileSize);
        fileInner5.innerHTML = file.fileType;

        fileElement.appendChild(fileInner1);
        fileElement.appendChild(fileInner2);
        fileElement.appendChild(fileInner3);
        fileElement.appendChild(fileInner4);
        fileElement.appendChild(fileInner5);

        // fileElement.onclick = () => showFileDetails(userID, file.fileID, file.filename, file.fileSize, file.fileType);
        fileElement.dataset.id = file.fileID;

        tmpList.push(fileElement);
    });
    return tmpList;
}

const getFileTypeClass = (fileType) => {
    switch (fileType) {
        case 'pdf':
            return 'pdf';
        case 'doc':
        case 'docx':
            return 'doc';
        case 'xls':
        case 'xlsx':
            return 'xls';
        case 'jpg':
        case 'jpeg':
        case 'png':
            return 'img';
        case 'mp4':
        case 'avi':
            return 'video';
        case 'zip':
        case 'rar':
        case 'tar':
        case 'gz':
            return 'zip';
        default:
            return '';
    }
}

const showFileDetails = async (userID, fileID, filename, fileSize, fileType) => {
    const fileDetails = document.getElementById('fileDetails');
    fileDetails.innerHTML = `
        <p>Filename: ${filename}</p>
        <p>File Size: ${formatSize(fileSize)}</p>
    `;

    const downloadBtn = document.getElementById('downloadBtn');
    downloadBtn.classList.remove('d-none');
    downloadBtn.onclick = () => fileDownloadHandler(userID, fileID, filename, fileSize);

    resetVideoPlayer();

    if (isVideoFile(fileType)) {
        const videoUrl = `/file/stream?userID=${encodeURIComponent(userID)}&fileID=${encodeURIComponent(fileID)}`;
        // const videoUrl = `http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4`;
        showVideo(videoUrl);
    }

    showModal();
}

const deleteFileDetail = async (userid, fileid) => {
    console.log("deleteFileDetail");
    const fileDetails = document.getElementById('fileDetails');
    fileDetails.innerHTML = `
        <p>userID: ${userid}</p>
        <p>fileID: ${fileid}</p>
    `
    console.log(userid);
    console.log(fileid);

    await fileDeleteHandler(userid, fileid);
}

const isVideoFile = (fileType) => {
    const videoExtensions = ['mp4', 'avi', 'mkv', 'mov', 'wmv'];
    return videoExtensions.includes(fileType);
}

const showVideo = (videoSourceUrl) => {
    const videoContainer = document.getElementById('videoContainer');
    const videoPlayer = document.getElementById('videoPlayer');
    const videoSourceElement = document.getElementById('videoSource');

    console.log(!videoContainer)
    console.log(!videoPlayer)
    console.log(!videoSourceElement)

    // Check if the elements are found
    if (!videoContainer || !videoPlayer || !videoSourceElement) {
        console.error('Video container, player, or source element not found.');
        return;
    }

    videoSourceElement.src = videoSourceUrl;
    videoPlayer.load();

    videoContainer.classList.remove('d-none');

    videojs(videoPlayer, {
        controls: true,
        autoplay: false,
        preload: 'auto'
    });
};

const resetVideoPlayer = () => {
    const videoContainer = document.getElementById('videoContainer');
    const videoPlayer = document.getElementById('videoPlayer');
    const videoSourceElement = document.getElementById('videoSource');

    // 기존 Video.js 인스턴스 제거
    if (videoPlayer) {
        videojs(videoPlayer).dispose();
    }

    // videoSourceElement src 속성 초기화
    // if (videoSourceElement) {
    //     videoSourceElement.src = '';
    // }

    // videoContainer를 숨김
    if (videoContainer) {
        videoContainer.innerHTML = '\n' +
            '                    <video id="videoPlayer" class="video-js vjs-default-skin" controls preload="auto" width="100%" height="264">\n' +
            '                        <source id="videoSource" src="" type="video/mp4">\n' +
            '                    </video>';
        videoContainer.classList.add('d-none');
    }

    // videoPlayer 요소를 다시 초기화
    // if (videoPlayer) {
    //     videoPlayer.innerHTML = '<source id="videoSource" src="" type="video/mp4">';
    // }
};

document.getElementById('fileModal').addEventListener('hidden.bs.modal', function () {
    const modalBackdrop = document.querySelector('.modal-backdrop');
    if (modalBackdrop) {
        modalBackdrop.parentNode.removeChild(modalBackdrop);
    }
    resetModal();
});

const fileDeleteHandler = async (userID, fileID) => {
    console.log("fileDeleteHandler");
    try {
        const response = await fetch("/file/deleteFile", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                userID: userID,
                fileID: fileID,
            }),
        });
        if (response.status === 200) {
            const result = await response.json();

            if (result.status === "success") {
                alert(result.message); // 성공 메시지 표시
                // 추가적인 성공 처리 로직이 있으면 여기에 추가
                //*************************************************************************
                await enterFolder($('#folderID').val());
                const row = document.querySelector('tr[data-file-id="${fileID}"]'); //^^^^^^^^
                console.log("success");
            } else {
                alert(result.message); // 실패 메시지 표시
            }
        } else {
            alert("File deletion failed with status code: " + response.status);
        }
    } catch (error) {
        console.error("Error:", error);
        alert("Error")
    }
};

const folderDeleteHandler = async (userID, folderID) => {
    console.log("folderDeleteHandler");
    try {
        const response = await fetch("/file/deleteFolder", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                userID: userID,
                folderID: folderID
            })
        });
        if (response.status === 200) {
            const result = await response.json();
            console.log(result);

            if (result.status === "success") {
                alert(result.message); // 성공 메시지 표시
                // 추가적인 성공 처리 로직이 있으면 여기에 추가
                await enterFolder($('#folderID').val());
                const row = document.querySelector('tr[folder-area="${folderID}"]');
                console.log("success");
            } else {
                alert(result.message); // 실패 메시지 표시
                console.log("failfail");
            }
        } else {
            alert("Folder deletion failed with status code: " + response.status);

        }
    } catch (error) {
        console.error("Error:", error);
        alert("Error")
    }
};

//TODO 파일 다운로드 및 업로드 중에 모달창 안꺼지게. 끈다면 작업이 중단 되도록 하게.
