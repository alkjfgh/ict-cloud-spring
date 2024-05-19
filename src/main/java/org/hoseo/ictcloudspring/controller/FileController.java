package org.hoseo.ictcloudspring.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hoseo.ictcloudspring.dao.FileService;
import org.hoseo.ictcloudspring.dto.File;
import org.hoseo.ictcloudspring.dto.Folder;
import org.hoseo.ictcloudspring.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/file/upload")
    public Object enterUpload(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
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
                System.out.println("return response");
                return ResponseEntity.ok(responseMap);
            }

            // 아닌 경우 기존 방식대로 ModelAndView 객체를 반환합니다.
            ModelAndView mav = new ModelAndView();
            mav.addAllObjects(responseMap);
            mav.setViewName("file/upload");
            System.out.println("return mav");
            return mav;
        }
    }


    @PostMapping("/file/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file,
                             @RequestParam("userID") String userID,
                             @RequestParam("storagePath") String storagePath,
                             @RequestParam("folderID") int folderID) {
        System.out.println("FileController file upload post request");
        // TODO 같은 이름의 파일 들어왔을 때 처리 생각해야함.

        int uploadFileSuccesses = fileService.uploadFile(file, userID, storagePath, folderID);

        return "redirect:upload?p=" + folderID;
    }

    @ResponseBody
    @PostMapping("/file/addFolder")
    public Map<String, Object> addFolder(@RequestBody Map<String, Object> requestData) {
        System.out.println("FileController add folder post request");

        Map<String, Object> response = new HashMap<>();

        int userID = (Integer) requestData.get("userID");
        String storagePath = (String) requestData.get("storagePath");
        int folderID = (Integer) requestData.get("folderID");
        String addFolderName = (String) requestData.get("addFolderName");
        System.out.println(storagePath);

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
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam("userID") int userID,
            @RequestParam("fileID") int fileID,
            @RequestParam("filename") String filename) {
        System.out.println("File Controller download file");
        System.out.println(fileID + ", " + userID + ", " + filename);

        byte[] fileContent = new byte[0];
        try {
            fileContent = fileService.getFile(userID, fileID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String mimeType = "application/octet-stream";
        String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(fileContent);
    }
}
