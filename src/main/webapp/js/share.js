
$(document).ready(function() {
    const shareId = window.location.pathname.split('/').pop();

    axios.get(`/share/info/${shareId}`)
        .then(function(response) {
            const shareInfo = response.data;
            if (shareInfo.permissionType === 'protected') {
                $('#passwordModal').modal('show');
            } else {
                console.log(response.data)
                showFileInfo(shareInfo);
            }
        })
        .catch(function(error) {
            console.error('Error fetching share info:', error);
            $('#downloadError').show();
        });

    $('#passwordForm').submit(function(e) {
        e.preventDefault();
        const password = $('#password').val();

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

        axios.post('/share/download', { shareId: shareId, password: password }, { responseType: 'blob' })
            .then(function(response) {
                const contentDisposition = response.headers['content-disposition'];
                const filename = contentDisposition.split('filename=')[1].replace(/"/g, '');
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', filename);
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
    $('#name').text('이름: ' + info.name);
    $('#size').text('크기: ' + formatSize(info.size));
    $('#info').show();
}

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