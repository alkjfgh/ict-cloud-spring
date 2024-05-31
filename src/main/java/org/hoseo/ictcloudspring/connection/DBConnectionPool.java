package org.hoseo.ictcloudspring.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.controller.AdminController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DBConnectionPool {

    private DataSource dataSource;
    private static final Logger logger = LogManager.getLogger(DBConnectionPool.class);

    @Autowired
    public DBConnectionPool(DataSource dataSource) {
        this.dataSource = dataSource;
        try (Connection con = dataSource.getConnection()) {
            logger.info("DB Connection Pool Success");
        } catch (Exception e) {
            logger.error("DB Connection Pool Failed");
            logger.error(e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
