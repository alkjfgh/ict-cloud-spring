package org.hoseo.ictcloudspring.dto;

public class AccessPermission {
    private int accessPermissionID;
    private int userID;
    private Integer fileID; // NULL 가능성이 있으므로 Integer 사용
    private Integer folderID; // NULL 가능성이 있으므로 Integer 사용
    private String accessType;

    // Getters and Setters
}
