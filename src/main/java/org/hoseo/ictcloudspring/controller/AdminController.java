package org.hoseo.ictcloudspring.controller;

import org.hoseo.ictcloudspring.dao.FileService;
import org.hoseo.ictcloudspring.dao.UserService;
import org.hoseo.ictcloudspring.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class AdminController {
    private FileService fileService;
    private UserService userService;

    @Autowired
    public AdminController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String enterAdminPage(){
        //TODO admin 확인 해야함.
        return "/admin/admin";
    }

    @ResponseBody
    @PostMapping("/admin/checkUploadFolderSize")
    public Map<String, Object> deleteFolder(@RequestBody Map<String, Object> requestData) {
        System.out.println("AdminController check upload folder size post request");

        boolean checkAdmin = userService.checkAdmin((User) requestData.get("user"));

        Map<String, Object> response = new HashMap<>();
        if (checkAdmin) {
            long uploadFolderSize = fileService.calculateFolderSize(new java.io.File("C:\\uploads"));
            long uploadFolderMaxSize = 1099511627776L;

            response.put("status", "success");
            response.put("message", "Uploads folder size calculated successfully");
            response.put("folderSize", uploadFolderSize);
            response.put("uploadFolderMaxSize", uploadFolderMaxSize);
        } else {
            response.put("status", "fail");
            response.put("message", "Uploads folder size calculated failed");
        }

        return response;
    }

    @ResponseBody
    @PostMapping("/admin/userStorageSizeList")
    public Map<String, Object> getUserStorageSizeList() {
        System.out.println("AdminController get user storageSize list post request");

        List<User> storageSizeList = fileService.getUserStorageSizeList();

        Map<String, Object> response = new HashMap<>();
        if (storageSizeList != null) {
            response.put("status", "success");
            response.put("message", "Uploads folder size calculated successfully");
            response.put("storageSizeList", storageSizeList);
            System.out.println("AdminController get user storageSize list post success");
        } else {
            response.put("status", "fail");
            response.put("message", "Uploads folder size calculated failed");
        }

        return response;
    }
}
