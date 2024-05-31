package org.hoseo.ictcloudspring.dao;

import org.hoseo.ictcloudspring.dto.DatabaseStatus;
import org.hoseo.ictcloudspring.dto.ServerStatus;
import org.hoseo.ictcloudspring.dto.StorageUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class SystemStatusService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ServerStatus getServerStatus() {
        // Get JVM uptime
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long uptimeMillis = runtimeMXBean.getUptime();
        String uptime = formatUptime(uptimeMillis);

        // Server status logic (for simplicity, we consider server running if this method is called)
        String status = "Running";

        return new ServerStatus(status, uptime);
    }

    public DatabaseStatus getDatabaseStatus() {
        String status = "Unknown";
        String dbSize = "Unknown";

        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            if (connection != null) {
                status = "Connected";
                dbSize = getDatabaseSize(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new DatabaseStatus(status, dbSize);
    }

    public StorageUsage getStorageUsage() {
        // Assume we are checking the root directory of the system
        File root = new File("/");
        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

        return new StorageUsage(usedSpace, totalSpace);
    }

    private String formatUptime(long uptimeMillis) {
        long uptimeSeconds = uptimeMillis / 1000;
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private String getDatabaseSize(Connection connection) throws SQLException {
        String dbSize = "0";

        try (ResultSet resultSet = connection.createStatement().executeQuery("SELECT\n" +
                "    table_schema AS \"Database\",\n" +
                "    ROUND(SUM(data_length + index_length)) AS \"Size (MB)\"\n" +
                "FROM\n" +
                "    information_schema.tables\n" +
                "WHERE\n" +
                "    table_schema = DATABASE()\n" +
                "GROUP BY\n" +
                "    table_schema")) {
            if (resultSet.next()) {
                dbSize = resultSet.getString(2);
            }
        }

        return dbSize;
    }
}