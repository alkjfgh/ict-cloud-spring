package org.hoseo.ictcloudspring.dto;

public class UploadHistory {
    private int uploadHistoryID;
    private int userID;
    private Integer fileID; // NULL 가능성이 있으므로 Integer 사용
    private Integer folderID; // NULL 가능성이 있으므로 Integer 사용
    private java.sql.Timestamp uploadDateTime;

    // Getters and Setters
}
