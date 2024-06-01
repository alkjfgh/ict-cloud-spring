package org.hoseo.ictcloudspring.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.connection.DBConnectionPool;
import org.hoseo.ictcloudspring.controller.UserController;
import org.hoseo.ictcloudspring.dto.Token;
import org.hoseo.ictcloudspring.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.*;

@Service
public class UserService {
    private final Connection con;
    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    public UserService(DBConnectionPool dbConnectionPool) {
        try {
            this.con = dbConnectionPool.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insertUser(User user) {
        logger.info("User Service insert user");
        logger.info("User: " + user);
        
        int isSignUp = 0;
        String query = "INSERT INTO Users(name, email, password, registrationDate)" +
                " VALUES(?, ?, ?, SYSDATE())"; // 회원가입한 유저 정보를 테이블에 저장 하는 쿼리
        logger.info(query);

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, user.getName());
            psmt.setString(2, user.getEmail());
            psmt.setString(3, user.getPassword());

            isSignUp = psmt.executeUpdate(); // 쿼리 실행해서 isSignUp에 성공했는지 여부 저장

            logger.info(user);
            logger.info("isSignUp: " + isSignUp);

            if (isSignUp == 1) { // 회원가입이 성공하면
                logger.info("회원가입 성공" + user);

                query = "SELECT UserId FROM Users WHERE Email = ?"; // 유저아이디를 가지고 옴
                try (PreparedStatement psmt2 = con.prepareStatement(query);) {
                    psmt2.setString(1, user.getEmail());

                    try (ResultSet rs = psmt2.executeQuery()) {
                        if (rs.next()) {
                            int userId = rs.getInt(1);
                            initFolder(userId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return isSignUp;
    }

    /**
     * userId에 맞는 공간에 root 폴더를 생성하는 메소드
     **/
    private int initFolder(int userId) {
        logger.info("User Service init folder");
        logger.info("userID: " + userId);

        int isInitFolder = 0;
        String query = "INSERT INTO Folders (FolderName, UserID, storagePath) VALUES (?, ?, ?)";
        logger.info(query);

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, "root");
            psmt.setInt(2, userId);
            psmt.setString(3, userId + File.separator + "root"); //root 폴더 생성

            isInitFolder = psmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return isInitFolder;
    }

    public boolean checkSignIn(User user) {
        logger.info("User Service check sign in");
        logger.info("User: " + user);

        boolean loggedIn = false;
        String query = "SELECT * FROM Users WHERE email = ? AND password = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, user.getEmail());
            psmt.setString(2, user.getPassword());

            try (ResultSet rs = psmt.executeQuery()) {
                // 로그인 성공 여부 확인
                if (rs.next()) {
                    loggedIn = true;
                    user.setUserID(rs.getInt("userID"));
                    user.setLevel(rs.getInt("level"));
                    user.setName(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return loggedIn;
    }

    public String generatedToken(String email) {
        logger.info("User Service generated token");
        logger.info("email: " + email);

        String query = "INSERT INTO Token values(?,?)";
        Token token = new Token(email);

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, token.getEmail());
            psmt.setString(2, token.getToken());

            int excuted = psmt.executeUpdate();
            if (excuted == 0) throw new SQLException("Can't insert token");
            return token.getToken();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return null;
    }

    public String getToken(String email) {
        logger.info("User Service get token");
        logger.info("email: " + email);

        String query = "SELECT token FROm Token WHERE email = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, email);

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("token");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return null;
    }

    public int deleteToken(String email) {
        logger.info("User Service delete token");
        logger.info("token: " + email);

        int executed = 0;
        String query = "DELETE FROM Token WHERE email = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, email);

            executed = psmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return executed;
    }

    public boolean checkAdmin(User user) {
        logger.info("User Service check admin");
        logger.info("User: " + user);

        if (user == null) return false;
        String query = "SELECT level FROM Users WHERE Email = ? AND Password = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, user.getEmail());
            psmt.setString(2, user.getPassword());

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    int level = rs.getInt(1);
                    if (level == 2) return true;
                }
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return false;
    }

    public int deleteUser(int userID) {
        logger.info("user service delete user");
        logger.info("userID: " + userID);

        int execute = 0;

        String query = "DELETE FROM Folders WHERE UserID = ? AND FolderName = 'root'";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);

            execute = psmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        query = "DELETE FROM Users WHERE UserID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);

            execute = psmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return execute;
    }

    public int editUser(User user) {
        logger.info("user service edit user");
        logger.info("User: " + user);

        int execute = 0;
        String query = "UPDATE Users SET Name = ?, Email = ?, Password = ?, Level = ?, storageMaxSize = ? WHERE UserID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, user.getName());
            psmt.setString(2, user.getEmail());
            psmt.setString(3, user.getPassword());
            psmt.setInt(4, user.getLevel());
            psmt.setLong(5, user.getStorageMaxSize());
            psmt.setInt(6, user.getUserID());

            execute = psmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return execute;

    }

    public int getUserInfo(User user) {
        logger.info("user service get user info");
        logger.info("User: " + user);

        int executed = 0;

        String query = "SELECT * FROM Users WHERE Email = ?";
        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, user.getEmail());

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    executed = 1;

                    user.setUserID(rs.getInt("userID"));
                    user.setName(rs.getString("name"));
                    user.setStorageMaxSize(rs.getLong("storageMaxSize"));
                    user.setRegistrationDate(rs.getTimestamp("registrationDate"));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return executed;
    }

    public int updatePassword(User user) {
        logger.info("user service get user info");
        logger.info("User: " + user);

        int executed = 0;

        String query = "UPDATE Users SET Password = ? WHERE UserID = ?";
        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, user.getPassword());
            psmt.setInt(2, user.getUserID());

            executed = psmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return executed;
    }

    public boolean isEmailAlready(String email) {
        logger.info("user service check email is already");
        logger.info("email: " + email);

        boolean check = false;

        String query = "SELECT * FROM Users WHERE Email = ?";
        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, email);

            try (ResultSet rs = psmt.executeQuery()) {
                if (!rs.next()) check = true;
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return check;
    }
}