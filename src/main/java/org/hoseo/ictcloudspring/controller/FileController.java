package org.hoseo.ictcloudspring.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.dao.FileService;
import org.hoseo.ictcloudspring.dto.File;
import org.hoseo.ictcloudspring.dto.Folder;
import org.hoseo.ictcloudspring.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class FileController {

    private final FileService fileService;
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/file/upload")
    public Object enterUpload(HttpServletRequest request) {
        logger.info("File Controller enter upload");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        logger.info("User: " + user);

        if (user == null) {
            return "redirect:../user/account";
        } else {
            int p = Integer.parseInt(request.getParameter("p") == null || request.getParameter("p").isEmpty() ? "0" : request.getParameter("p"));
            int userID = user.getUserID();
            String storagePath;
            int parentFolderID = 0;

            if (p == 0 || !fileService.isFolderId(userID, p)) {
                storagePath = userID + java.io.File.separator + "root";
                p = fileService.getFolderId(userID, storagePath);
            } else {
                storagePath = fileService.getStoragePath(userID, p);
                parentFolderID = fileService.getParentFolderId(p);
            }

            String storagePathJS = storagePath.replace("\\", "\\\\");
            List<File> fileList = fileService.getFilesByUserIdAndFolderId(userID, p);
            List<Folder> subFolderList = fileService.getSubFoldersByFolderId(p);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("fileList", fileList);
            responseMap.put("subFolderList", subFolderList);
            responseMap.put("userID", userID);
            responseMap.put("storagePath", storagePath);
            responseMap.put("p", p);
            responseMap.put("parentFolderID", parentFolderID);
            responseMap.put("removeUserIdPath", storagePath.replace(userID + java.io.File.separator, ""));
            responseMap.put("storagePathJS", storagePathJS);

            // AJAX 요청인 경우 JSON 형태로 데이터를 반환합니다.
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return ResponseEntity.ok(responseMap);
            }

            // 아닌 경우 기존 방식대로 ModelAndView 객체를 반환합니다.
            ModelAndView mav = new ModelAndView();
            mav.addAllObjects(responseMap);
            mav.setViewName("file/upload");
            return mav;
        }
    }


    @PostMapping("/file/upload")
    public ResponseEntity<String> fileUpload(@RequestParam("file") MultipartFile file,
                                             @RequestParam("userID") String userID,
                                             @RequestParam("storagePath") String storagePath,
                                             @RequestParam("folderID") int folderID) {
        logger.info("File Controller file upload post request");
        // TODO 같은 이름의 파일 들어왔을 때 처리 생각해야함.

        int uploadFileSuccesses = fileService.uploadFile(file, userID, storagePath, folderID, FilenameUtils.getExtension(file.getOriginalFilename()));

        if (uploadFileSuccesses == 1) {
            return ResponseEntity.ok("File uploaded successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    @ResponseBody
    @PostMapping("/file/addFolder")
    public Map<String, Object> addFolder(@RequestBody Map<String, Object> requestData) {
        logger.info("File Controller add folder post request");

        Map<String, Object> response = new HashMap<>();

        int userID = (Integer) requestData.get("userID");
        String storagePath = (String) requestData.get("storagePath");
        int folderID = (Integer) requestData.get("folderID");
        String addFolderName = (String) requestData.get("addFolderName");
        logger.info("storagePath: " + storagePath);

        int addFolderSuccesses = fileService.addFolder(userID, storagePath, folderID, addFolderName);

        if (addFolderSuccesses == 1) {
            response.put("status", "success");
            response.put("message", "addFolder 성공");
        } else {
            response.put("status", "fail");
            response.put("message", "addFolder 실패");
        }

        return response;
    }

    @GetMapping("/file/download")
    public ResponseEntity<StreamingResponseBody> downloadFile(
            @RequestParam("userID") int userID,
            @RequestParam("fileID") int fileID,
            @RequestParam("filename") String filename) {
        logger.info("File Controller download file");
        logger.info("fileID: " + fileID + ", userID: " + userID + ", filename: " + filename);

        StreamingResponseBody responseBody = outputStream -> {
            InputStream inputStream = null;
            try {
                inputStream = fileService.getFileStream(userID, fileID);
                byte[] buffer = new byte[1024 * 1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    try {
                        outputStream.write(buffer, 0, bytesRead);
                        outputStream.flush();
                    } catch (IOException e) {
                        if (e.getCause() instanceof org.apache.catalina.connector.ClientAbortException) {
                            logger.error("클라이언트가 다운로드를 중단했습니다: " + e.getMessage());
                            break;
                        } else {
                            throw e;
                        }
                    }
                }
            } catch (IOException e) {
                if (e instanceof org.apache.catalina.connector.ClientAbortException) {
                    logger.error("클라이언트가 다운로드를 중단했습니다: " + e.getMessage());
                } else {
                    logger.error(e.getLocalizedMessage());
                }
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        logger.error(e.getLocalizedMessage());
                    }
                }
            }
        };

        String mimeType = "application/octet-stream";
        String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(responseBody);
    }

    @GetMapping("/file/stream")
    public ResponseEntity<StreamingResponseBody> streamVideo(
            @RequestParam("userID") int userID,
            @RequestParam("fileID") int fileID) {
        logger.info("File Controller stream video");

        StreamingResponseBody responseBody = outputStream -> {
            try {
                InputStream inputStream = fileService.getFileStream(userID, fileID);
                byte[] buffer = new byte[1024 * 1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/mp4")) // 비디오 파일 형식에 맞게 조정
                .body(responseBody);
    }

    @GetMapping("/file/storageSize")
    public ResponseEntity<long[]> fileUpload(@RequestParam("userID") int userID) {
        logger.info("File Controller storageSize get request");

        long[] sizes = fileService.getStorageSize(userID);

        if (sizes != null) {
            logger.info("storageMaxSize: " + sizes[0] + " totalSize: " + sizes[1]);
            return ResponseEntity.ok(sizes);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ResponseBody
    @PostMapping("/file/initAll")
    public Map<String, Object> initFileAndFolder(@RequestBody Map<String, Object> requestData) {
        logger.info("File Controller init File and Folder post request");

        Map<String, Object> response = new HashMap<>();

        int userID = Integer.parseInt(String.valueOf(requestData.get("userID")));
        logger.info("userID: " + userID);

        int initSuccesses = fileService.initFileAndFolder(userID);

        if (initSuccesses == 1) {
            response.put("status", "success");
            response.put("message", "init File and Folder 성공");
        } else {
            response.put("status", "fail");
            response.put("message", "init File and Folder 실패");
        }

        return response;
    }

    @ResponseBody
    @PostMapping("/file/deleteFile")
    public Map<String, Object> deleteFile(@RequestBody Map<String, Object> requestData) {
        logger.info("FileController delete file post request");

        Map<String, Object> response = new HashMap<>();

        int userID = Integer.parseInt(String.valueOf(requestData.get("userID")));
        int fileID = Integer.parseInt(String.valueOf(requestData.get("fileID")));
        logger.info("userID: " + userID + " fileID: " + fileID);

        int deleteFileSuccesses = fileService.deleteFile(userID, fileID);

        if (deleteFileSuccesses == 1) {
            response.put("status", "success");
            response.put("message", "delete File 성공");
        } else {
            response.put("status", "fail");
            response.put("message", "delete File 실패");
        }
        return response;
    }

    @ResponseBody
    @PostMapping("/file/deleteFolder")
    public Map<String, Object> deleteFolder(@RequestBody Map<String, Object> requestData) {
        logger.info("FileController delete Folder post request");

        Map<String, Object> response = new HashMap<>();

        int userID = Integer.parseInt(String.valueOf(requestData.get("userID")));
        int folderID = Integer.parseInt(String.valueOf(requestData.get("folderID")));
        logger.info("userID: " + userID + " folderID: " + folderID);

        int deleteFolderSuccesses = fileService.deleteFolder(userID, folderID);

        if (deleteFolderSuccesses == 1) {
            response.put("status", "success");
            response.put("message", "delete Folder 성공");
        } else {
            response.put("status", "fail");
            response.put("message", "delete Folder 실패");
        }

        return response;
    }

    @GetMapping("/file/searchFiles")
    @ResponseBody
    public ResponseEntity<List<File>> searchFiles(@RequestParam(required = false) String filename,
                                                  @RequestParam(required = false) String startDate,
                                                  @RequestParam(required = false) String endDate,
                                                  @RequestParam(required = false) Long minFileSize,
                                                  @RequestParam(required = false) Long maxFileSize,
                                                  @RequestParam(required = false) Integer userId) {
        logger.info("FileController Search Files");

        Timestamp startTimestamp = null;
        Timestamp endTimestamp = null;
        if (startDate != null && !startDate.isEmpty()) {
            startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            endTimestamp = Timestamp.valueOf(endDate + " 23:59:59");
        }

        List<File> files = fileService.searchFiles(filename, startTimestamp, endTimestamp, minFileSize, maxFileSize, userId);
        return ResponseEntity.ok(files);
    }


    // TODO 다운로드 속도 정할지 고민
}
