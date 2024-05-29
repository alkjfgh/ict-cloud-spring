package org.hoseo.ictcloudspring.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.connection.DBConnectionPool;
import org.hoseo.ictcloudspring.controller.UserController;
import org.hoseo.ictcloudspring.dto.File;
import org.hoseo.ictcloudspring.dto.Folder;
import org.hoseo.ictcloudspring.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class FileService {
    private final String uploadFolderPath = "C:\\uploads";
    private final Connection con;
    private PreparedStatement psmt;
    private ResultSet rs;
    private final String SEPARATOR = java.io.File.separator;
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    public FileService(DBConnectionPool dbConnectionPool) throws SQLException {
        con = dbConnectionPool.getConnection();

        java.io.File f = new java.io.File(uploadFolderPath);

        if (!f.exists()) {
            logger.info("MKDIR UPLOAD PATH: " + f.mkdir());
        }
    }

    public int uploadFile(MultipartFile file, String userID, String storagePath, int folderID, String extension) {
        logger.info("File Service upload file");

        if (file.isEmpty()) {
            logger.warn("파일이 비어있습니다.");
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

        boolean csr = checkStorageRemain(Integer.parseInt(userID), newFile.getFileSize());
        if (!csr) return 0;

        boolean writeSuccesses = fileWrite(uploadFolderPath + SEPARATOR + storagePath + SEPARATOR + fileName, file);

        if (writeSuccesses) {
            String query = "INSERT INTO Files(UserID, FolderID, Filename, FileSize, StoragePath, UploadDate, LastModifiedDate, fileType) " +
                    "VALUES (?, ?, ?, ?, ?, SYSDATE(), SYSDATE(), ?);";
            logger.info("query: " + query);
            logger.info("file: " + newFile);

            try (PreparedStatement psmt = con.prepareStatement(query);) {
                psmt.setInt(1, newFile.getUserID());
                psmt.setInt(2, newFile.getFolderID());
                psmt.setString(3, newFile.getFilename());
                psmt.setLong(4, newFile.getFileSize());
                psmt.setString(5, newFile.getStoragePath());
                psmt.setString(6, newFile.getFileType());

                boolean executed = psmt.execute();
                if (executed) {
                    logger.info("파일 db 업로드 성공: " + storagePath);
                    logger.info("file: " + newFile);
                }

            } catch (Exception e) {
                logger.error("파일 db 업로드 실패: " + e.getLocalizedMessage());
            }
        } else {
            logger.warn("파일 쓰기 실패");
            return 0;
        }

        return 1;
    }

    public boolean checkStorageRemain(int userID, long fileSize) {
        logger.info("File Service check storage remain");
        logger.info("userID: " + userID + " fileSize: " + fileSize);

        long[] sizes = getStorageSize(userID);
        return sizes[0] >= sizes[1] + fileSize;
    }

    /***
     * @param userID
     * @return long[] sizes = {storageMaxSize, totalSize}
     * null is something wrong happened
     */
    public long[] getStorageSize(int userID) {
        logger.info("File Service Get Storage Size");
        logger.info("userID: " + userID);

        long[] sizes = new long[2];

        String query = "SELECT storageMaxSize FROM Users WHERE userID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) sizes[0] = rs.getLong(1);
                else throw new SQLException("Storage Max Size is not available");
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }

        logger.info(Arrays.toString(sizes));

        sizes[1] = calculateTotalFileSize(userID);

        return sizes;
    }

    public long calculateTotalFileSize(int userID) {
        logger.info("File Service calculate total filesize");
        logger.info("userID: " + userID);

        String query = "SELECT COALESCE(SUM(fileSize), 0) AS totalFileSize " +
                "FROM Files " +
                "WHERE userID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            logger.info(query);
            psmt.setInt(1, userID);

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
                else throw new SQLException("Storage total Size is not available");
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            return 0;
        }
    }

    private boolean fileWrite(String path, MultipartFile file) {
        logger.info("File Service file write");
        logger.info("path: " + path);

        try {
            java.io.File dest = new java.io.File(path);

            // 파일이 저장될 폴더가 없는 경우, 모든 상위 폴더를 포함하여 생성
            java.io.File parentDir = dest.getParentFile();
            if (!parentDir.exists()) {
                boolean dirsCreated = parentDir.mkdirs();
                if (!dirsCreated) {
                    logger.warn("필요한 폴더를 생성할 수 없습니다.");
                    return false;
                }
            }

            file.transferTo(dest);

            return true;
        } catch (IOException e) {
            logger.error("파일 저장 실패: " + e.getLocalizedMessage());
            return false;
        }
    }

    public int getFolderId(int userID, String storagePath) {
        logger.info("File Service get folder id");
        logger.info("userID: " + userID + " storagePath: " + storagePath);

        int folderID = 0;

        String query = "SELECT FolderID FROM Folders WHERE UserID = ? AND StoragePath = ?";
        logger.info(query);
        logger.info(userID);
        logger.info(storagePath);

        try (PreparedStatement psmt = con.prepareStatement(query);) {
//            storagePath = storagePath.replace("\\", "\\\\");

            psmt.setInt(1, userID);
            psmt.setNString(2, storagePath);
            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    folderID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return folderID;
    }

    public List<File> getFilesByUserIdAndFolderId(int userId, int folderId) {
        logger.info("File Service get files by userID and FolderID");
        logger.info("userID: " + userId + " folderID: " + folderId);

        List<File> files = new ArrayList<>();

        String query = "SELECT * FROM Files WHERE UserID = ? AND FolderID = ?";
        logger.info(query);

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
            logger.error(e.getLocalizedMessage());
        }

        return files;
    }

    public List<Folder> getSubFoldersByFolderId(int folderId) {
        logger.info("File Service get sub folders by folderID");
        logger.info("folderID: " + folderId);

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
            logger.error(e.getLocalizedMessage());
        }

        return folders;
    }

    public int addFolder(int userID, String storagePath, int folderID, String addFolderName) {
        logger.info("File Service add folder");
        logger.info("userID: " + userID + " storagePath: " + storagePath + " folderID: " + folderID + " addFolderName: " + addFolderName);

        int addFolderSuccesses = 0;

        String query = "INSERT INTO Folders(ParentFolderID, UserID, FolderName, StoragePath) VALUES(?,?,?,?)";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, folderID);
            psmt.setInt(2, userID);
            psmt.setString(3, addFolderName);
            psmt.setString(4, storagePath + SEPARATOR + addFolderName);

            addFolderSuccesses = psmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return addFolderSuccesses;
    }

    public String getStoragePath(int userID, int folderID) {
        logger.info("File Service get storagePath");
        logger.info("userID: " + userID + " folderID: " + folderID);

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
            logger.error(e.getLocalizedMessage());
        }

        return storagePath;
    }

    public boolean isFolderId(int userID, int folderID) {
        logger.info("File Service is folderID");
        logger.info("userID: " + userID + " folderID: " + folderID);

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
            logger.error(e.getLocalizedMessage());
        }

        return isFI;
    }

    public int getParentFolderId(int folderID) {
        logger.info("File Service get parent folderID");
        logger.info("folderID: " + folderID);

        int parentFolderID = 0;

        String query = "SELECT parentFolderID FROM Folders WHERE folderID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, folderID);

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    parentFolderID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return parentFolderID;
    }

    public InputStream getFileStream(int userID, int fileID) throws IOException {
        logger.info("File Service get file stream");
        logger.info("userID: + " + userID + " fileID: " + fileID);

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
            logger.error("Database error while retrieving file path: " + e.getLocalizedMessage());
        }

        // Example path; replace with actual file path logic
        String realFilePath = uploadFolderPath + SEPARATOR + storagePath + SEPARATOR + filename;
        return new FileInputStream(new java.io.File(realFilePath));
    }

    public boolean isVideoFile(String fileType) {
        return fileType.equalsIgnoreCase("mp4") || fileType.equalsIgnoreCase("avi");
    }

    public int initFileAndFolder(int userID) {
        logger.info("File Service Get Storage Size");
        logger.info("userID: " + userID);

        int executed;

        // Get all file paths to delete from filesystem
        List<String> filePaths = new ArrayList<>();
        String fileQuery = "SELECT storagePath FROM Files WHERE userID = ?";

        try (PreparedStatement psmt = con.prepareStatement(fileQuery)) {
            psmt.setInt(1, userID);
            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    filePaths.add(rs.getString("storagePath"));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            return 0;
        }

        // Delete all files from database
        String query = "DELETE FROM Files WHERE userID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);

            executed = psmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            return 0;
        }

        // Delete all file paths from filesystem
        for (String path : filePaths) {
            Path filePath = Paths.get(uploadFolderPath, path);
            try {
                if (Files.isDirectory(filePath)) {
                    // Recursively delete directory contents
                    Files.walk(filePath)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(java.io.File::delete);
                } else {
                    Files.deleteIfExists(filePath);
                }
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
        }

        // Get all folder paths to delete from filesystem
        List<String> folderPaths = new ArrayList<>();
        query = "WITH RECURSIVE FolderHierarchy AS (" +
                "    SELECT FolderID, ParentFolderID, storagePath " +
                "    FROM Folders " +
                "    WHERE UserID = ? " +
                "    UNION ALL " +
                "    SELECT f.FolderID, f.ParentFolderID, f.storagePath " +
                "    FROM Folders f " +
                "    INNER JOIN FolderHierarchy fh ON f.ParentFolderID = fh.FolderID " +
                ") " +
                "SELECT storagePath FROM FolderHierarchy";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);
            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    folderPaths.add(rs.getString("storagePath"));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            return 0;
        }

        // Delete all folders from database
        query = "WITH RECURSIVE FolderHierarchy AS (" +
                "    SELECT FolderID, ParentFolderID " +
                "    FROM Folders " +
                "    WHERE UserID = ? " +
                "    UNION ALL " +
                "    SELECT f.FolderID, f.ParentFolderID " +
                "    FROM Folders f " +
                "    INNER JOIN FolderHierarchy fh ON f.ParentFolderID = fh.FolderID " +
                ") " +
                "DELETE FROM Folders " +
                "WHERE FolderID IN (SELECT FolderID FROM FolderHierarchy) " +
                "  AND ParentFolderID IS NOT NULL";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);

            executed = psmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            return 0;
        }

        // Delete all file paths from filesystem
        for (String path : filePaths) {
            Path filePath = Paths.get(uploadFolderPath, path);
            try {
                if (Files.exists(filePath)) { // Check if file or directory exists
                    if (Files.isDirectory(filePath)) {
                        // Recursively delete directory contents
                        Files.walk(filePath)
                                .sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                .forEach(java.io.File::delete);

                        // Once the directory is empty, delete it
                        Files.deleteIfExists(filePath);
                    } else {
                        Files.deleteIfExists(filePath); // Delete file
                    }
                } else {
                    logger.info("File or directory does not exist: " + filePath);
                }
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
        }

        return 1;
    }

    public int deleteFile(int userID, int fileID) {
        logger.info("File Service delete file");
        logger.info("userID: " + userID + " fileID: " + fileID);

        int executed = 0;

        // Get file path to delete from filesystem
        String filePath = null;
        String query = "SELECT storagePath FROM Files WHERE userID = ? AND fileID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);
            psmt.setInt(2, fileID);

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    filePath = rs.getString("storagePath");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            return 0;
        }

        // Delete file from database
        query = "DELETE FROM Files WHERE userID = ? AND fileID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, userID);
            psmt.setInt(2, fileID);

            executed = psmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            return 0;
        }

        // Delete file from filesystem
        if (filePath != null) {
            Path path = Paths.get(uploadFolderPath, filePath);
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
                return 0;
            }
        }

        return executed;
    }

    public int deleteFolder(int userID, int folderID) {
        logger.info("File Service delete folder");
        logger.info("userID: " + userID + ", folderID: " + folderID);

        int executed = 0;

        // Get folder path to delete from filesystem
        String folderPath = null;
        String query = "SELECT storagePath FROM Folders WHERE FolderID = ? AND UserID = ?";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, folderID);
            psmt.setInt(2, userID);
            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    folderPath = rs.getString("storagePath");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            return 0;
        }

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
            logger.error(e.getLocalizedMessage());
            return 0;
        }

        // 하위 폴더가 없고, 폴더 내 파일이 있는 경우 파일 삭제 로직 수행
        if (subfolderCount == 0) {
            // 폴더 내 파일 삭제
            List<Integer> fileIDs = new ArrayList<>();
            String getFileIDsQuery = "SELECT FileID FROM Files WHERE FolderID = ? AND UserID = ?";
            try (PreparedStatement psmt = con.prepareStatement(getFileIDsQuery)) {
                psmt.setInt(1, folderID);
                psmt.setInt(2, userID);
                try (ResultSet rs = psmt.executeQuery()) {
                    while (rs.next()) {
                        fileIDs.add(rs.getInt("FileID"));
                    }
                }
            } catch (SQLException e) {
                logger.error(e.getLocalizedMessage());
                return 0;
            }

            for (int fileID : fileIDs) {
                deleteFile(userID, fileID);
            }

            // 폴더 삭제 쿼리
            String queryDeleteFolder = "DELETE FROM Folders WHERE FolderID = ? AND UserID = ?";
            try (PreparedStatement psmt = con.prepareStatement(queryDeleteFolder)) {
                psmt.setInt(1, folderID);
                psmt.setInt(2, userID);
                executed = psmt.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getLocalizedMessage());
                return 0;
            }

            // 폴더를 파일 시스템에서 삭제
            if (folderPath != null) {
                Path path = Paths.get(uploadFolderPath, folderPath);
                try {
                    Files.walk(path)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(java.io.File::delete);
                } catch (IOException e) {
                    logger.error(e.getLocalizedMessage());
                    return 0;
                }
            }
        } else {
            // 하위 폴더가 있는 경우 폴더 삭제 실패 메시지 반환
            logger.warn("하위 폴더가 존재하여 폴더 삭제를 할 수 없습니다.");
            return 0;
        }

        return executed;
    }

    public long calculateFolderSize(java.io.File folder) {
        logger.info("File Service calculate folderSize");

        long length = 0;
        java.io.File[] files = folder.listFiles();

        if (files != null) {
            for (java.io.File file : files) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += calculateFolderSize(file);
                }
            }
        }

        return length;
    }

    public List<User> getUserStorageSizeList() {
        logger.info("File Service get user storageSizeList");

        List<User> userStorageSizeList = new ArrayList<>();

        String query = "SELECT * FROM Users";

        try (PreparedStatement psmt = con.prepareStatement(query)) {
            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();

                    user.setUserID(rs.getInt("userID"));
                    user.setLevel(rs.getInt("level"));
                    user.setEmail(rs.getString("email"));
                    user.setStorageMaxSize(rs.getLong("storageMaxSize"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setRegistrationDate(rs.getTimestamp("registrationDate"));
                    user.setTotalSize(calculateTotalFileSize(user.getUserID()));

                    userStorageSizeList.add(user);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

        return userStorageSizeList;
    }
}

//TODO deleteFolder, deleteFile 안 터지는지 확인(init 처럼 바꿔야 할지도?)
