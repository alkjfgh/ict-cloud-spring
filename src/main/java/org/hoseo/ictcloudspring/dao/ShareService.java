package org.hoseo.ictcloudspring.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.connection.DBConnectionPool;
import org.hoseo.ictcloudspring.dto.ShareInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShareService {
    private final Connection con;
    private static final Logger logger = LogManager.getLogger(ShareService.class);

    @Autowired
    public ShareService(DBConnectionPool dbConnectionPool) throws SQLException {
        this.con = dbConnectionPool.getConnection();
    }

    public Optional<ShareInfo> getShareInfo(String shareId) {
        logger.info("Share Service get share info for " + shareId);
        String query = "SELECT * FROM ShareInfo WHERE ShareID = ?";
        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, shareId);
            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    ShareInfo shareInfo = new ShareInfo();
                    shareInfo.setShareID(rs.getString("ShareID"));
                    shareInfo.setOwnerID(rs.getInt("OwnerID"));
                    shareInfo.setItemID(rs.getInt("ItemID"));
                    shareInfo.setItemType(rs.getString("ItemType"));
                    shareInfo.setPermissionType(rs.getString("PermissionType"));
                    shareInfo.setExpirationDate(rs.getDate("ExpirationDate"));
                    shareInfo.setCreationDate(rs.getDate("CreationDate"));
                    shareInfo.setSharePassword(rs.getString("SharePassword"));
                    return Optional.of(shareInfo);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching share info: ", e);
        }
        return Optional.empty();
    }

    public String createShare(ShareInfo shareInfo) {
        logger.info("Share Service create share for " + shareInfo);
        String shareId = generateShareId();
        String query = "INSERT INTO ShareInfo (ShareID, OwnerID, ItemID, ItemType, PermissionType, ExpirationDate, CreationDate, SharePassword) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, shareId);
            psmt.setInt(2, shareInfo.getOwnerID());
            psmt.setInt(3, shareInfo.getItemID());
            psmt.setString(4, shareInfo.getItemType());
            psmt.setString(5, shareInfo.getPermissionType());
            psmt.setDate(6, new Date(shareInfo.getExpirationDate().getTime()));
            psmt.setDate(7, new Date(System.currentTimeMillis()));
            psmt.setString(8, shareInfo.getSharePassword());

            psmt.executeUpdate();
            return shareId;
        } catch (SQLException e) {
            logger.error("Error creating share: ", e);
        }
        return null;
    }

    private String generateShareId() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(UUID.randomUUID().toString().getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error generating share ID: ", e);
        }
        return null;
    }

    public boolean checkPassword(String shareId, String password) {
        logger.info("Checking password for share ID: " + shareId);
        String query = "SELECT SharePassword FROM ShareInfo WHERE ShareID = ?";
        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, shareId);
            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("SharePassword");
                    return storedPassword.equals(password);
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking password: ", e);
        }
        return false;
    }
}