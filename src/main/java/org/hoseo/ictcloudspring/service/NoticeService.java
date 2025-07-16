package org.hoseo.ictcloudspring.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.connection.DBConnectionPool;
import org.hoseo.ictcloudspring.dto.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public List<Notice> getNoticeList() {
        logger.info("NoticeService get notice list");
        List<Notice> list = new ArrayList<Notice>();

        String query = "SELECT * FROM notices";
        try(PreparedStatement psmt = con.prepareStatement(query)){
            try(ResultSet rs = psmt.executeQuery()){
                while(rs.next()) {
                    Notice notice = new Notice();
                    notice.setTitle(rs.getString("title"));
                    notice.setContent(rs.getString("content"));
                    notice.setCreated_at(rs.getTimestamp("created_at"));
                    list.add(notice);
                }
            }
        }catch (SQLException e) {
            logger.error("Error get notice list: ", e);
            return null;
        }

        return list;
    }
}
