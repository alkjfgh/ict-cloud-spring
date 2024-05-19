$(document).ready(() => {
});

document.addEventListener("DOMContentLoaded", function () {
    const dragDropArea = document.getElementsByClassName('drag-drop-area')[0];

    dragDropArea.addEventListener('dragover', function (e) {
        e.preventDefault(); // 기본 이벤트 방지
        e.stopPropagation(); // 이벤트 전파 방지
        e.dataTransfer.dropEffect = 'copy'; // 드래그 중 아이콘 변경
    });

    dragDropArea.addEventListener('drop', function (e) {
        e.preventDefault(); // 기본 이벤트 방지
        e.stopPropagation(); // 이벤트 전파 방지

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

const showModal = (modalId) => {
    const modal = new bootstrap.Modal(document.getElementById(modalId));
    modal.show();
};

const hideModal = (modalId) => {
    const modal = new bootstrap.Modal(document.getElementById(modalId));
    modal.hide();
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

const updateProgress = (progressBarId, downloadDetailsId, startTime, loaded, total) => {
    const progressBar = document.getElementById(progressBarId);
    const downloadDetails = document.getElementById(downloadDetailsId);

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

    downloadDetails.innerText = `
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

    const modalId = 'uploadModal';
    const progressBarId = 'progressBar';
    const uploadDetailsId = 'uploadDetails';
    showModal(modalId);

    let startTime = new Date().getTime();

    try {
        const response = await axios.post('/file/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            onUploadProgress: (progressEvent) => {
                updateProgress(progressBarId, uploadDetailsId, startTime, progressEvent.loaded, progressEvent.total);
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
        // hideModal(modalId);
    }
};

const fileDownloadHandler = async (userID, fileID, filename, fileSize) => {
    console.log('download file');

    const modalId = 'downloadModal';
    const progressBarId = 'downloadProgressBar';
    const downloadDetailsId = 'downloadDetails';
    showModal(modalId);

    let startTime = new Date().getTime();

    try {
        const response = await axios({
            url: `/file/download?userID=${encodeURIComponent(userID)}&fileID=${encodeURIComponent(fileID)}&filename=${encodeURIComponent(filename)}`,
            method: 'GET',
            responseType: 'blob',
            onDownloadProgress: (progressEvent) => {
                updateProgress(progressBarId, downloadDetailsId, startTime, progressEvent.loaded, fileSize /*progressEvent.total*/);
            }
        });

        if (response.status === 200) {
            const blob = new Blob([response.data], { type: 'application/octet-stream' });
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
        // hideModal(modalId);
    }
};

const addFolderHandler = async (userID, folderID, storagePath) => {
    await fetch("/file/addFolder", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            userID: userID,
            storagePath: storagePath,
            folderID: folderID,
            addFolderName: $("#addFolderName").val()
        }),
    }).then((response) => {
        if (response.status === 200) enterFolder(folderID);
        else alert("add folder failed");
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
        console.log(data);

        const testView = document.getElementsByClassName('test-view')[0];
        testView.innerHTML = '<div class="path">now path: ' + data.storagePath + '</div>\n' +
            '                removeUserIdPath: ' + data.removeUserIdPath + '\n' +
            '                parentFolderID: ' + data.parentFolderID + '';

        const addFile = document.getElementsByClassName('add_file')[0];
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
            '                        <th>download</th>\n' +
            '                    </tr>';

        if (data.removeUserIdPath !== 'root') {
            fileListElement.innerHTML += '<tr>\n' +
                '                            <td colspan="5" class="folder-area" onclick="enterFolder(' + data.parentFolderID + ')">...</td>\n' +
                '                        </tr>';
        }

        const folderList = updateFolderList(data.subFolderList);
        folderList.forEach(element => {
            fileListElement.appendChild(element);
        });

        const fileList = updateFileList(data.fileList, data.userID);
        fileList.forEach(element => {
            fileListElement.appendChild(element);
        });

        // fileListElement.load(location.href + ' .file-list-table');
    } else {
        alert("Failed to load folder data.");
    }
}

const updateFolderList = (folderList) => {
    console.log(folderList);
    let tmpList = [];

    folderList.forEach(folder => {
        let folderElement = document.createElement("tr");
        let folderInner = document.createElement("td");

        folderInner.className = "folder-area";
        folderInner.colSpan = 5;
        folderInner.onclick = () => enterFolder(folder.folderID);
        folderInner.innerHTML = folder.folderName;

        folderElement.appendChild(folderInner);
        tmpList.push((folderElement));
    });

    return tmpList;
}


const updateFileList = (fileList, userID) => {
    console.log(fileList);
    let tmpList = [];

    fileList.forEach(file => {
        let fileElement = document.createElement("tr");
        let fileInner1 = document.createElement("td");
        let fileInner2 = document.createElement("td");
        let fileInner3 = document.createElement("td");
        let fileInner4 = document.createElement("td");
        let fileInner5 = document.createElement("td");
        let downloadBtn = document.createElement("div");

        fileInner1.innerHTML = file.filename;
        fileInner2.innerHTML = file.uploadDate;
        fileInner3.innerHTML = file.lastModifiedDate;
        fileInner4.innerHTML = file.fileSize;

        // downloadBtn.onclick = downLoadFile(userID, file.fileID, file.filename);
        downloadBtn.onclick = () => fileDownloadHandler(userID, file.fileID, file.filename, file.fileSize);
        downloadBtn.innerHTML = 'download';
        downloadBtn.className = 'download-btn';

        fileInner5.appendChild(downloadBtn);

        fileElement.appendChild(fileInner1);
        fileElement.appendChild(fileInner2);
        fileElement.appendChild(fileInner3);
        fileElement.appendChild(fileInner4);
        fileElement.appendChild(fileInner5);

        tmpList.push(fileElement);
    });

    return tmpList;
}