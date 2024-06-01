$(document).ready(function() {
    const shareInfo = '<%= request.getAttribute("shareInfo") %>';

    if (shareInfo.sharePassword) {
        $('#passwordModal').modal('show');
    } else {
        showFileInfo(shareInfo);
    }

    $('#passwordForm').submit(function(e) {
        e.preventDefault();
        const password = $('#password').val();
        const shareId = '<%= request.getAttribute("shareInfo").getShareID() %>';

        axios.post('/share/checkPassword', { shareId: shareId, password: password })
            .then(function(response) {
                if (response.status === 200) {
                    $('#passwordModal').modal('hide');
                    showFileInfo(response.data);
                }
            })
            .catch(function(error) {
                console.error('Error checking password:', error);
                $('#passwordError').show();
            });
    });

    $('#downloadForm').submit(function(e) {
        e.preventDefault();
        const password = $('#password').val();
        const shareId = '<%= request.getAttribute("shareInfo").getShareID() %>';

        axios.post('/share/download', { shareId: shareId, password: password }, { responseType: 'blob' })
            .then(function(response) {
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', '<%= request.getAttribute("shareInfo").getFilename() %>');
                document.body.appendChild(link);
                link.click();
            })
            .catch(function(error) {
                console.error('Error downloading file:', error);
                $('#downloadError').show();
            });
    });
});

function showFileInfo(info) {
    $('#fileName').text('파일 이름: ' + info.filename);
    $('#fileSize').text('파일 크기: ' + info.fileSize + ' bytes');
    $('#fileInfo').show();
}