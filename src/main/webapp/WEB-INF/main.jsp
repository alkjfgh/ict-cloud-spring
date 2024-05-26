<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>">
    <title>index</title>
</head>
<body>
<div class="main-body">
    <div class="body-header">
        <strong>ICT</strong> Drive
    </div>

    <div class="body-middle">
        <span class="body-text">Easy and secure access <br> to your content</span> <br>
        <span class="body-addtext">Store, share, and collaborate on files and folders <br> across your phone, tablet, and computer.</span>
    </div>

    <div class="body-link">
        <button type="button" class="btn btn-secondary" onclick="location.href = 'user/account'">account</button>
        <button type="button" class="btn btn-secondary" onclick="location.href = 'file/upload'">upload</button>
    </div>

    <div class="first-text">
        <p class="body-text">Built-in protection against <br>
            malware, spam, and ransomware</p>
        <p class="body-addtext">With Drive, you can access your files in a secure, encrypted way. Files <br>
            shared with you are automatically scanned and deleted if malware, spam, ransomware,
            or phishing is detected, and because Drive is cloud-based,<br>
            there's no need to store files locally, minimizing the risk to your device.</p>
    </div>

    <div class="second-text">
        <p class="body-text">User-centered collaboration <br>
            apps that maximize teamwork</p>
        <p class="body-addtext">Drive integrates seamlessly with cloud-based collaboration apps <br>
            like Docs, Sheets, Slides, and others that help teams effectively <br>
            create and collaborate on real-time content.</p>
    </div>

    <div class="third-text">
        <p class="body-text">Integrate with tools and <br>
            apps your team already uses</p>
        <p class="body-addtext">Drive integrates with and complements the technology your team <br>
            already uses. Collaborate on Microsoft Office files without converting <br>
            file formats, and edit and save over 100 file types, including PDFs, <br>
            CAD files, images, and more.</p>
    </div>
</div>
</body>
</html>
