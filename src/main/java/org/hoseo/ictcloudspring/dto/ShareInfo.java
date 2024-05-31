package org.hoseo.ictcloudspring.dto;

import java.util.Date;

public class ShareInfo {
    private String shareID;
    private int ownerID;

    @Override
    public String toString() {
        return "ShareInfo{" +
                "shareID='" + shareID + '\'' +
                ", ownerID=" + ownerID +
                ", itemID=" + itemID +
                ", itemType='" + itemType + '\'' +
                ", permissionType='" + permissionType + '\'' +
                ", expirationDate=" + expirationDate +
                ", creationDate=" + creationDate +
                ", sharePassword='" + sharePassword + '\'' +
                '}';
    }

    private int itemID;
    private String itemType;
    private String permissionType;
    private Date expirationDate;
    private Date creationDate;
    private String sharePassword;

    // Getters and Setters
    public String getShareID() {
        return shareID;
    }

    public void setShareID(String shareID) {
        this.shareID = shareID;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getSharePassword() {
        return sharePassword;
    }

    public void setSharePassword(String sharePassword) {
        this.sharePassword = sharePassword;
    }
}
