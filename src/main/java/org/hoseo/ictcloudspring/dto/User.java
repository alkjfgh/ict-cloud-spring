package org.hoseo.ictcloudspring.dto;

import java.sql.Timestamp;

public class User {
    private int userID;
    private String name;
    private String email;
    private String password;
    private java.sql.Timestamp registrationDate;
    private int level;

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", registrationDate=" + registrationDate +
                ", level=" + level +
                ", storageMaxSize=" + storageMaxSize +
                '}';
    }

    public long getStorageMaxSize() {
        return storageMaxSize;
    }

    public void setStorageMaxSize(long storageMaxSize) {
        this.storageMaxSize = storageMaxSize;
    }

    private long storageMaxSize;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getUserID() { return userID; }

    public void setUserID(int userID) { this.userID = userID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Timestamp getRegistrationDate() { return registrationDate; }

    public void setRegistrationDate(Timestamp registrationDate) { this.registrationDate = registrationDate; }

    // Getters and Setters
}
