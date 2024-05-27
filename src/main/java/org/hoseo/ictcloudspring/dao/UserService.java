package org.hoseo.ictcloudspring.dao;

import org.hibernate.annotations.processing.SQL;
import org.hoseo.ictcloudspring.connection.DBConnectionPool;
import org.hoseo.ictcloudspring.dto.Token;
import org.hoseo.ictcloudspring.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.*;

@Service
public class UserService {
    private final Connection con;

    @Autowired
    public UserService(DBConnectionPool dbConnectionPool) {
        try {
            this.con = dbConnectionPool.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insertUser(User user) {
        PreparedStatement psmt = null;
        int isSignUp = 0;
        ResultSet rs = null;

        try {
            String query = "INSERT INTO Users(name, email, password, registrationDate)" +
                    " VALUES(?, ?, ?, SYSDATE())"; // 회원가입한 유저 정보를 테이블에 저장 하는 쿼리
            System.out.println(query);
            psmt = con.prepareStatement(query);

            psmt.setString(1, user.getName());
            psmt.setString(2, user.getEmail());
            psmt.setString(3, user.getPassword());

            isSignUp = psmt.executeUpdate(); // 쿼리 실행해서 isSignUp에 성공했는지 여부 저장

            System.out.println(user);
            System.out.println(isSignUp);

            if (isSignUp == 1) { // 회원가입이 성공하면
                System.out.println("회원가입 성공" + user);

                query = "SELECT UserId FROM Users WHERE Email = ?"; // 유저아이디를 가지고 옴
                psmt = con.prepareStatement(query);
                psmt.setString(1, user.getEmail());

                rs = psmt.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    initFolder(userId);
                }
            }
        } catch (SQLException e) {
            System.out.println("insert user: " + e.getMessage());
        } finally {
            try {
                if (psmt != null) psmt.close();
                if (rs != null) rs.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return isSignUp;
    }

    /**
     * userId에 맞는 공간에 root 폴더를 생성하는 메소드
     **/
    private int initFolder(int userId) {
        PreparedStatement psmt = null;
        int isInitFolder = 0;

        try {
            String query = "INSERT INTO Folders (FolderName, UserID, storagePath) VALUES (?, ?, ?)";
            System.out.println(query);
            psmt = con.prepareStatement(query);

            psmt.setString(1, "root");
            psmt.setInt(2, userId);
            psmt.setString(3, userId + File.separator + "root"); //root 폴더 생성

            isInitFolder = psmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("initFolder: " + e.getMessage());
        } finally {
            try {
                if (psmt != null) psmt.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return isInitFolder;
    }

    public boolean checkSignIn(User user) {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        boolean loggedIn = false;

        try {
            String query = "SELECT * FROM Users WHERE email = ? AND password = ?";
            psmt = con.prepareStatement(query);

            psmt.setString(1, user.getEmail());
            psmt.setString(2, user.getPassword());

            rs = psmt.executeQuery();

            // 로그인 성공 여부 확인
            if (rs.next()) {
                loggedIn = true;
                user.setUserID(rs.getInt("userID"));
                user.setLevel(rs.getInt("level"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            // 자원을 해제합니다.
            try {
                if (psmt != null) psmt.close();
                if (rs != null) rs.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return loggedIn;
    }

    public String generatedToken(String email) {
        System.out.println("generated token: " + email);
        String query = "INSERT INTO Token values(?,?)";
        Token token = new Token(email);

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, token.getEmail());
            psmt.setString(2, token.getToken());

            int excuted = psmt.executeUpdate();
            if (excuted == 0) throw new SQLException("Token");
            return token.getToken();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getToken(String email) {
        System.out.println("get token: " + email);
        String query = "SELECT token FROm Token WHERE email = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, email);

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("token");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int deleteToken(String email) {
        System.out.println("delete token: " + email);
        int excuted = 0;
        String query = "DELETE FROM Token WHERE email = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setString(1, email);

            excuted = psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return excuted;
    }

    public boolean checkAdmin(User user) {
        System.out.println("check admin: " + user);
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
            e.printStackTrace();
        }

        return false;
    }

    public int deleteUser(int userID) {
        System.out.println("user service delete user: " + userID);

        int execute = 0;
        String query = "DELETE FROM Folders WHERE UserID = ? AND FolderName = 'root'";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);

            execute = psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        query = "DELETE FROM Users WHERE UserID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);

            execute = psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return execute;
    }
}