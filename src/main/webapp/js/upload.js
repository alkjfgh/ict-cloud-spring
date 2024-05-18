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

const enterFolder = (p) => {
    location.href = "/file/upload?p=" + p;
}