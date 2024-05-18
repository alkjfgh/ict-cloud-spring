package org.hoseo.ictcloudspring.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hoseo.ictcloudspring.dao.FileService;
import org.hoseo.ictcloudspring.dto.File;
import org.hoseo.ictcloudspring.dto.Folder;
import org.hoseo.ictcloudspring.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView enterUpload(HttpServletRequest request) {
        System.out.println("FileController user session check");
        ModelAndView mav = new ModelAndView();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) mav.setViewName("redirect:../user/account");
        else {
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

            mav.addObject("fileList", fileList);
            mav.addObject("subFolderList", subFolderList);
            mav.addObject("userID", userID);
            mav.addObject("storagePath", storagePath);
            mav.addObject("p", p);
            mav.addObject("storagePathJS", storagePathJS);
            mav.addObject("removeUserIdPath", storagePath.replace(userID + java.io.File.separator, ""));
            mav.addObject("parentFolderID", parentFolderID);

            System.out.println("==============================");
            System.out.println(userID);
            System.out.println(p);
            System.out.println(storagePath);
            System.out.println(storagePathJS);
            System.out.println(fileList);
            System.out.println(subFolderList);
            System.out.println(storagePath.replace(userID + java.io.File.separator, ""));
            System.out.println(parentFolderID);

            mav.setViewName("file/upload");
        }

        return mav;
    }


    @PostMapping("/file/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file,
                             @RequestParam("userID") String userID,
                             @RequestParam("storagePath") String storagePath,
                             @RequestParam("folderID") int folderID) {
        System.out.println("FileController file upload post request");

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
}
