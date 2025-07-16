package org.hoseo.ictcloudspring.dto;

public class DatabaseStatus {
    private String status;
    private String dbSize;

    @Override
    public String toString() {
        return "DatabaseStatus{" +
                "status='" + status + '\'' +
                ", dbSize='" + dbSize + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public DatabaseStatus(String status, String dbSize) {
        this.status = status;
        this.dbSize = dbSize;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDbSize() {
        return dbSize;
    }

    public void setDbSize(String dbSize) {
        this.dbSize = dbSize;
    }
}
