package org.hoseo.ictcloudspring.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.connection.DBConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class NoticeService {
    private final Connection con;
    private static final Logger logger = LogManager.getLogger(NoticeService.class);

    @Autowired
    public NoticeService(DBConnectionPool dbConnectionPool) throws SQLException {
        this.con = dbConnectionPool.getConnection();
    }

    public int saveNotice(String title, String content) {
        logger.info("NoticeService save notice");

        String query = "INSERT INTO notices (title, content, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, title);
            psmt.setString(2, content);
            return psmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving notice: ", e);
            return 0;
        }
    }
}
