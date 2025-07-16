package org.hoseo.ictcloudspring.dto;

public class StorageUsage {
    private long usedSpace;
    private long totalSpace;

    @Override
    public String toString() {
        return "StorageUsage{" +
                "usedSpace=" + usedSpace +
                ", totalSpace=" + totalSpace +
                '}';
    }

    public long getUsedSpace() {
        return usedSpace;
    }

    public StorageUsage(long usedSpace, long totalSpace) {
        this.usedSpace = usedSpace;
        this.totalSpace = totalSpace;
    }

    public void setUsedSpace(long usedSpace) {
        this.usedSpace = usedSpace;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }
}
