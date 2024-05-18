package org.hoseo.ictcloudspring.connection;

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

    @Autowired
    public DBConnectionPool(DataSource dataSource) {
        this.dataSource = dataSource;
        try (Connection con = dataSource.getConnection()) {
            System.out.println("DB Connection Pool Success");
        } catch (Exception e) {
            System.out.println("DB Connection Pool Failed");
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
