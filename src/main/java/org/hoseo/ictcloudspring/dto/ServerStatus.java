package org.hoseo.ictcloudspring.dto;

public class ServerStatus {
    private String status;
    private String uptime;

    public ServerStatus(String status, String uptime) {
        this.status = status;
        this.uptime = uptime;
    }

    @Override
    public String toString() {
        return "ServerStatus{" +
                "status='" + status + '\'' +
                ", uptime='" + uptime + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }
}
