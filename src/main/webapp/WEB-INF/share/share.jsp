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
            <form id="downloadForm" class="mt-4">
                <div class="form-group">
                    <label for="password">비밀번호</label>
                    <input type="password" class="form-control" id="password" placeholder="비밀번호를 입력하세요 (필요한 경우)">
                </div>
                <button type="submit" class="btn btn-primary btn-block">다운로드</button>
            </form>
            <div id="downloadError" class="alert alert-danger mt-4" style="display: none;">
                다운로드 실패: 접근 권한이 없거나 파일이 만료되었습니다.
            </div>
        </div>
    </div>
</div>

<script>
    document.getElementById('downloadForm').addEventListener('submit', function (e) {
        e.preventDefault();

        const password = document.getElementById('password').value;
        const shareId = window.location.pathname.split('/').pop();

        axios.post('/share/download', { shareId: shareId, password: password }, { responseType: 'blob' })
            .then(function (response) {
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', 'shared_file');
                document.body.appendChild(link);
                link.click();
            })
            .catch(function (error) {
                console.error('Error downloading file:', error);
                document.getElementById('downloadError').style.display = 'block';
            });
    });
</script>
</body>
</html>