package org.hoseo.ictcloudspring.dao;

import org.hoseo.ictcloudspring.connection.DBConnectionPool;
import org.hoseo.ictcloudspring.dto.File;
import org.hoseo.ictcloudspring.dto.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FileService {
    private final String uploadFolderPath = "C:\\uploads";
    private final Connection con;
    private PreparedStatement psmt;
    private ResultSet rs;
    private final String SEPARATOR = java.io.File.separator;

    @Autowired
    public FileService(DBConnectionPool dbConnectionPool) throws SQLException {
        con = dbConnectionPool.getConnection();

        java.io.File f = new java.io.File(uploadFolderPath);

        if (!f.exists()) {
            System.out.println("MKDIR UPLOAD PATH: " + f.mkdir());
        }
    }

    public int uploadFile(MultipartFile file, String userID, String storagePath, int folderID, String extension) {
        if (file.isEmpty()) {
            System.out.println("파일이 비어있습니다.");
            return 0;
        }

        String fileName = file.getOriginalFilename();

        File newFile = new File();
        newFile.setUserID(Integer.parseInt(userID));
        newFile.setStoragePath(storagePath);
        newFile.setFolderID(folderID);
        newFile.setFilename(fileName);
        newFile.setFileSize(file.getSize());
        newFile.setFileType(extension);
        newFile.setUploadDate(new Timestamp(System.currentTimeMillis()));

        boolean csr = checkStorageRemain(userID, newFile.getFileSize());
        if (!csr) return 0;

        boolean writeSuccesses = fileWrite(uploadFolderPath + SEPARATOR + storagePath + SEPARATOR + fileName, file);

        if (writeSuccesses) {
            PreparedStatement psmt = null;
            try {
                String query = "INSERT INTO Files(UserID, FolderID, Filename, FileSize, StoragePath, UploadDate, LastModifiedDate, fileType) " +
                        "VALUES (?, ?, ?, ?, ?, SYSDATE(), SYSDATE(), ?);";
                System.out.println(query);
                System.out.println(newFile);

                psmt = con.prepareStatement(query);

                psmt.setInt(1, newFile.getUserID());
                psmt.setInt(2, newFile.getFolderID());
                psmt.setString(3, newFile.getFilename());
                psmt.setLong(4, newFile.getFileSize());
                psmt.setString(5, newFile.getStoragePath());
                psmt.setString(6, newFile.getFileType());

                boolean executed = psmt.execute();
                if (executed) {
                    System.out.println("파일 db 업로드 성공: " + storagePath);
                    System.out.println(newFile);
                }

            } catch (Exception e) {
                System.out.println("파일 db 업로드 실패: " + e.getMessage());
            } finally {
                if (psmt != null) {
                    try {
                        psmt.close();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("파일 쓰기 실패");
            return 0;
        }

        return 1;
    }

    public boolean checkStorageRemain(String userID, long fileSize) {
        long[] sizes = getStorageSize(userID);
        return sizes[0] >= sizes[1] + fileSize;
    }

    /***
     * @param userID
     * @return long[] sizes = {storageMaxSize, totalSize}
     * null is something wrong happened
     */
    public long[] getStorageSize(String userID) {
        System.out.println("File Service Get Storage Size: " + userID);
        long[] sizes = new long[2];

        String query = "SELECT storageMaxSize FROM Users WHERE userID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, Integer.parseInt(userID));

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) sizes[0] = rs.getLong(1);
                else throw new SQLException("Storage Max Size is not available");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        System.out.println(Arrays.toString(sizes));
        query = "SELECT COALESCE(SUM(fileSize), 0) AS totalFileSize\n" +
                "FROM Files\n" +
                "WHERE userID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            System.out.println(query);
            psmt.setInt(1, Integer.parseInt(userID));

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) sizes[1] = rs.getLong(1);
                else throw new SQLException("Storage total Size is not available");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return sizes;
    }

    private boolean fileWrite(String path, MultipartFile file) {
        try {
            java.io.File dest = new java.io.File(path);

            // 파일이 저장될 폴더가 없는 경우, 모든 상위 폴더를 포함하여 생성
            java.io.File parentDir = dest.getParentFile();
            if (!parentDir.exists()) {
                boolean dirsCreated = parentDir.mkdirs();
                if (!dirsCreated) {
                    System.out.println("필요한 폴더를 생성할 수 없습니다.");
                    return false;
                }
            }

            file.transferTo(dest);

            return true;
        } catch (IOException e) {
            System.out.println("파일 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public int getFolderId(int userID, String storagePath) {
        int folderID = 0;

        try {
            String query = "SELECT FolderID FROM Folders WHERE UserID = ? AND StoragePath = ?";
//            storagePath = storagePath.replace("\\", "\\\\");
            System.out.println(query);
            System.out.println(userID);
            System.out.println(storagePath);

            psmt = con.prepareStatement(query);
            psmt.setInt(1, userID);
            psmt.setNString(2, storagePath);
            rs = psmt.executeQuery();

            if (rs.next()) {
                folderID = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (psmt != null) psmt.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return folderID;
    }

    public List<File> getFilesByUserIdAndFolderId(int userId, int folderId) {
        System.out.println("getFilesByUserIDAndFolderID()");
        List<File> files = new ArrayList<>();

        String query = "SELECT * FROM Files WHERE UserID = ? AND FolderID = ?";
        System.out.println(query);
        System.out.println(userId + ", " + folderId);

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userId);
            psmt.setInt(2, folderId);

            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    File file = new File();

                    file.setFileID(rs.getInt("FileID"));
                    file.setUserID(rs.getInt("UserID"));
                    file.setFolderID(rs.getInt("FolderID"));
                    file.setFilename(rs.getString("Filename"));
                    file.setFileSize(rs.getLong("FileSize"));
                    file.setStoragePath(rs.getString("StoragePath"));
                    file.setFileType(rs.getString("fileType"));
                    file.setUploadDate(rs.getTimestamp("UploadDate"));
                    file.setLastModifiedDate(rs.getTimestamp("LastModifiedDate"));

                    files.add(file);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return files;
    }

    public List<Folder> getSubFoldersByFolderId(int folderId) {
        List<Folder> folders = new ArrayList<>();

        String query = "SELECT * FROM Folders WHERE ParentFolderID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, folderId);

            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    Folder folder = new Folder();
                    folder.setFolderID(rs.getInt("FolderID"));
                    folder.setParentFolderID(rs.getInt("ParentFolderID"));
                    folder.setUserID(rs.getInt("UserID"));
                    folder.setFolderName(rs.getString("FolderName"));
                    folder.setStoragePath(rs.getString("StoragePath"));
                    folders.add(folder);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return folders;
    }

    public int addFolder(int userID, String storagePath, int folderID, String addFolderName) {
        System.out.println("File Service add folder");
        int addFolderSuccesses = 0;

        System.out.println("=====================");
        System.out.println(userID + ", " + storagePath + ", " + folderID + ", " + addFolderName);

        String query = "INSERT INTO Folders(ParentFolderID, UserID, FolderName, StoragePath) VALUES(?,?,?,?)";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, folderID);
            psmt.setInt(2, userID);
            psmt.setString(3, addFolderName);
            psmt.setString(4, storagePath + SEPARATOR + addFolderName);

            addFolderSuccesses = psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return addFolderSuccesses;
    }

    public String getStoragePath(int userID, int folderID) {
        String storagePath = "root";

        String query = "SELECT storagePath FROM Folders WHERE userID = ? AND folderID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);
            psmt.setInt(2, folderID);

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    storagePath = rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return storagePath;
    }

    public boolean isFolderId(int userID, int folderID) {
        boolean isFI = false;

        String query = "SELECT * FROM Folders WHERE userID = ? AND folderID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);
            psmt.setInt(2, folderID);

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    isFI = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isFI;
    }

    public int getParentFolderId(int p) {
        int parentFolderID = 0;

        String query = "SELECT parentFolderID FROM Folders WHERE folderID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, p);

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    parentFolderID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return parentFolderID;
    }

    public InputStream getFileStream(int userID, int fileID) throws IOException {
        // TODO user 검증 여기서? 생각해봐야함
        String query = "SELECT storagePath, filename FROM Files WHERE fileID = ?";
        String storagePath = "";
        String filename = "";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, fileID);
            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    storagePath = rs.getString(1);
                    filename = rs.getString(2);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IOException("Database error while retrieving file path", e);
        }

        // Example path; replace with actual file path logic
        String realFilePath = uploadFolderPath + SEPARATOR + storagePath + SEPARATOR + filename;
        return new FileInputStream(new java.io.File(realFilePath));
    }

    public boolean isVideoFile(String fileType) {
        return fileType.equalsIgnoreCase("mp4") || fileType.equalsIgnoreCase("avi");
    }

    public int initFileAndFolder(int userID) {
        System.out.println("File Service Get Storage Size: " + userID);
        int excuted = 0;

        String query = "DELETE FROM Files WHERE userID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);

            excuted = psmt.executeUpdate();
//            if(excuted == 0) throw new SQLException("Can't init Files: " + userID);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        query = "WITH RECURSIVE FolderHierarchy AS (\n" +
                "    SELECT FolderID, ParentFolderID\n" +
                "    FROM Folders\n" +
                "    WHERE UserID = ?\n" +
                "    UNION ALL\n" +
                "    SELECT f.FolderID, f.ParentFolderID\n" +
                "    FROM Folders f\n" +
                "             INNER JOIN FolderHierarchy fh ON f.ParentFolderID = fh.FolderID\n" +
                ")\n" +
                "DELETE FROM Folders\n" +
                "WHERE FolderID IN (SELECT FolderID FROM FolderHierarchy)\n" +
                "  AND ParentFolderID IS NOT NULL";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);

            excuted = psmt.executeUpdate();
//            if(excuted == 0) throw new SQLException("Can't init Folders: " + userID);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        return excuted;
    }

    public int deleteFile(int userID, int fileID) {
        System.out.println("File Service delete file: " + userID + ", " + fileID);
        int excuted = 0;

        String query = "DELETE FROM Files WHERE userID = ? AND fileID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);
            psmt.setInt(2, fileID);

            excuted = psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        return excuted;
    }

    public int deleteFolder(int userID, int folderID) {
        System.out.println("File Service delete folder: " + userID + ", " + folderID);
        int executed = 0;

        // 폴더 내에 하위 폴더가 있는지 확인
        String queryCheckSubfolders = "SELECT COUNT(*) FROM Folders WHERE ParentFolderID = ?";
        int subfolderCount = 0;
        try (PreparedStatement psmt = con.prepareStatement(queryCheckSubfolders)) {
            psmt.setInt(1, folderID);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                subfolderCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        // 하위 폴더가 없고, 폴더 내 파일이 있는 경우 파일 삭제 로직 수행
        if (subfolderCount == 0) {
            // 폴더 내 파일 삭제 (deleteFile 메소드 호출 가정)
            deleteFile(userID, folderID); // 이 부분은 deleteFile 메소드의 정확한 시그니처에 맞게 수정해야 합니다.

            // 폴더 삭제 쿼리
            String queryDeleteFolder = "DELETE FROM Folders WHERE FolderID = ? AND UserID = ?";
            try (PreparedStatement psmt = con.prepareStatement(queryDeleteFolder)) {
                psmt.setInt(1, folderID);
                psmt.setInt(2, userID);
                executed = psmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        } else {
            System.out.println("Folder contains subfolders and cannot be deleted.");
        }

        return executed;
    }
}
