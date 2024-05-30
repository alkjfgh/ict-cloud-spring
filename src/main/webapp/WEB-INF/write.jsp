<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Write</title>
</head>
<body>
<form action="announcements.jsp" method="post">
    <table class="table">
        <tr>
            <td class="header"><h2>writing</h2></td>
        </tr>

        <tr>
            <td class="header">Title</td>
        </tr>

        <tr>
            <td><input type="text" placeholder="enter a title" name="title"></td>
        </tr>

        <tr>
            <td class="header">Comment</td>
        </tr>

        <tr>
            <td><textarea placeholder="enter your content" name="detail"></textarea></td>
        </tr>

        <tr>
            <td><input type="submit" class="submit" value="enrollment" onclick="alert('completed!')"></td>
        </tr>
    </table>
</form>
</body>
</html>
