package org.hoseo.ictcloudspring.dto;

public class Folder {
    private int folderID;
    private Integer parentFolderID; // NULL 가능성이 있으므로 Integer 사용
    private int userID;
    private String folderName;
    private String storagePath;

    public int getFolderID() {
        return folderID;
    }

    public void setFolderID(int folderID) {
        this.folderID = folderID;
    }

    public Integer getParentFolderID() {
        return parentFolderID;
    }

    public void setParentFolderID(Integer parentFolderID) {
        this.parentFolderID = parentFolderID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    // Getters and Setters
}
