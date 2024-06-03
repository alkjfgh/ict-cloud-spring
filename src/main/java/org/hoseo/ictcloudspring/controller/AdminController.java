package org.hoseo.ictcloudspring.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.service.FileService;
import org.hoseo.ictcloudspring.service.NoticeService;
import org.hoseo.ictcloudspring.service.UserService;
import org.hoseo.ictcloudspring.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class AdminController {
    private final FileService fileService;
    private final UserService userService;
    private final NoticeService noticeService;
    private static final Logger logger = LogManager.getLogger(AdminController.class);

    @Autowired
    public AdminController(FileService fileService, UserService userService, NoticeService noticeService) {
        this.fileService = fileService;
        this.userService = userService;
        this.noticeService = noticeService;
    }

    @GetMapping("/admin")
    public String enterAdminPage(HttpServletRequest request) {
        logger.info("Admin Controller enter admin page");
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        logger.info("User: " + user);

        boolean checkAdmin = userService.checkAdmin(user);

        if(checkAdmin) return "/admin/admin";
        else return "redirect:/main";
    }

    @ResponseBody
    @PostMapping("/admin/checkUploadFolderSize")
    public Map<String, Object> checkUploadFolderSize(HttpServletRequest request) {
        logger.info("AdminController check upload folder size post request");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        logger.info("User: " + user);

        boolean checkAdmin = userService.checkAdmin(user);

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
    public Map<String, Object> getUserStorageSizeList(HttpServletRequest request) {
        logger.info("AdminController get user storageSize list post request");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        logger.info("User: " + user);

        boolean checkAdmin = userService.checkAdmin(user);

        Map<String, Object> response = new HashMap<>();
        if (checkAdmin) {
            List<User> storageSizeList = fileService.getUserStorageSizeList();

            if (storageSizeList != null) {
                response.put("status", "success");
                response.put("message", "Uploads folder size calculated successfully");
                response.put("storageSizeList", storageSizeList);
                logger.info("AdminController get user storageSize list post success");
            } else {
                response.put("status", "fail");
                response.put("message", "Uploads folder size calculated failed");
            }
        } else {
            response.put("status", "fail");
            response.put("message", "check level");
        }

        return response;
    }

    @ResponseBody
    @PostMapping("/admin/uploadNotice")
    public Map<String, Object> uploadNotice(HttpServletRequest request) {
        logger.info("AdminController upload notice post request");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        logger.info("User: " + user);

        boolean checkAdmin = userService.checkAdmin(user);

        Map<String, Object> response = new HashMap<>();
        if (checkAdmin) {
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            logger.info("Title: " + title);
            logger.info("Content: " + content);

            int result = noticeService.saveNotice(title, content);
            if (result > 0) {
                response.put("status", "success");
                response.put("message", "Notice uploaded successfully");
            } else {
                response.put("status", "fail");
                response.put("message", "Notice upload failed");
            }
        } else {
            response.put("status", "fail");
            response.put("message", "check level");
        }

        return response;
    }
}
