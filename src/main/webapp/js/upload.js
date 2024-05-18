$(document).ready(() => {
});

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
        if (response.status === 200) location.reload();
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

        // const fileListElement = document.getElementById(".file-list-table");
        // const fileListElement = $('.file-list-table');
        const fileListElement = document.getElementsByClassName('file-list-table')[0];
        fileListElement.innerHTML = '<tr>\n' +
            '                        <th>filename</th>\n' +
            '                        <th>UploadDate</th>\n' +
            '                        <th>LastModifiedDate</th>\n' +
            '                        <th>download</th>\n' +
            '                    </tr>';

        if (data.removeUserIdPath !== 'root') {
            fileListElement.innerHTML += '<tr>\n' +
                '                            <td colspan="4" class="folder-area" onclick="enterFolder(' + data.parentFolderID + ')">...</td>\n' +
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
        folderInner.colSpan = 4;
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
        let downloadBtn = document.createElement("div");

        fileInner1.innerHTML = file.filename;
        fileInner2.innerHTML = file.uploadDate;
        fileInner3.innerHTML = file.lastModifiedDate;

        // downloadBtn.onclick = downLoadFile(userID, file.fileID, file.filename);
        downloadBtn.onclick = () => downLoadFile(userID, file.fileID, file.filename);
        downloadBtn.innerHTML = 'download';
        downloadBtn.className = 'download-btn';

        fileInner4.appendChild(downloadBtn);

        fileElement.appendChild(fileInner1);
        fileElement.appendChild(fileInner2);
        fileElement.appendChild(fileInner3);
        fileElement.appendChild(fileInner4);

        tmpList.push(fileElement);
    });

    return tmpList;
}

const downLoadFile = async (userID, fileID, filename) => {
    console.log('download file');
    //TODO 모달창 띄워서 다운로드 창 만들면 좋을듯

    const url = `/file/download?userID=${encodeURIComponent(userID)}&fileID=${encodeURIComponent(fileID)}&filename=${encodeURIComponent(filename)}`;

    const response = await fetch(url, {
        method: "GET",
        headers: {
            'Accept': 'application/octet-stream', // 바이너리 데이터 수신 요청
            'X-Requested-With': 'XMLHttpRequest'
        }
    });

    // 이하 로직은 동일하게 유지합니다.
    if (response.ok) {
        console.log('response ok');
        const blob = await response.blob();
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
}
