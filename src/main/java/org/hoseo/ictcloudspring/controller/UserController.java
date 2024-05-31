package org.hoseo.ictcloudspring.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.dao.FileService;
import org.hoseo.ictcloudspring.dao.UserService;
import org.hoseo.ictcloudspring.dto.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;
    private final FileService fileService;
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @RequestMapping("/user")
    public String userHome() {
        logger.info("UserController Home");

        return "redirect:user/account";
    }

    @GetMapping("/user/info")
    public String userInfo(HttpServletRequest request) {
        logger.info("UserController info");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/account";

        logger.info("User: " + user);

        return "/user/userInfo";
    }

    @GetMapping("/user/checkSession")
    public ResponseEntity<Map<String, Object>> checkSession(HttpServletRequest request) {
        logger.info("UserController check session");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        System.out.println("User: " + user);

        Map<String, Object> response = new HashMap<>();
        if (user != null) response.put("check", true);
        else response.put("check", false);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/getInfo")
    public ResponseEntity<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        logger.info("UserController get user info");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        System.out.println("User: " + user);

        Map<String, Object> response = new HashMap<>();
        if (user != null) {
            int executed = userService.getUserInfo(user);

            if (executed == 1) {
                long totalSize = fileService.calculateTotalFileSize(user.getUserID());
                user.setTotalSize(totalSize);
                response.put("user", user);

                return ResponseEntity.ok(response);
            }
        }

        response.put("message", "get user info failed");
        return ResponseEntity.badRequest().body(response);
    }

    @ResponseBody
    @PostMapping("/user/updatePassword")
    public ResponseEntity<Map<String, Object>> updatePassword(HttpServletRequest request, @RequestBody Map<String, Object> requestData) {
        logger.info("UserController update password");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String changePassword = String.valueOf(requestData.get("changePassword"));
        logger.info("User: " + user);
        logger.info("changePassword: " + changePassword);

        Map<String, Object> response = new HashMap<>();
        if (user != null) {
            user.setPassword(changePassword);
            int executed = userService.updatePassword(user);

            if (executed == 1) {
                return ResponseEntity.ok(response);
            }
        }

        response.put("message", "update password failed");
        return ResponseEntity.badRequest().body(response);
    }

    @ResponseBody
    @PostMapping("/user/signOut")
    public ResponseEntity<Map<String, Object>> updatePassword(HttpServletRequest request) {
        logger.info("UserController sign out user");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        logger.info("User: " + user);

        Map<String, Object> response = new HashMap<>();
        if (user != null) {
            int executed = fileService.initFileAndFolder(user.getUserID());

            if (executed == 1) {
                executed = userService.deleteUser(user.getUserID());

                if (executed == 1) {
                    session.removeAttribute("user");
                    return ResponseEntity.ok(response);
                }
            }
        }

        response.put("message", "sign out failed");
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/user/account")
    public String isSignInSession(HttpServletRequest request, HttpServletResponse response) {
        logger.info("UserController user session check");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            logger.info("User: " + user);
            return "redirect:../file/upload";
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        return "user/account";
    }

    @ResponseBody
    @PostMapping("/user/signIn")
    public Map<String, Object> signIn(@ModelAttribute User user, HttpServletRequest request) throws UnsupportedEncodingException {
        logger.info("User Controller signIn");
        logger.info("User: " + user);

        Map<String, Object> response = new HashMap<>();
        boolean isSignIn = userService.checkSignIn(user);

        if (isSignIn) {
            request.getSession().setAttribute("user", user);

            response.put("status", "success");
            response.put("message", "로그인 성공");
            response.put("level", user.getLevel());
        } else {
            response.put("status", "fail");
            response.put("message", "로그인 실패");
        }

        return response;
    }

    @PostMapping("/user/signUp")
    public ResponseEntity<String> signUp(@RequestBody User user) {
        logger.info("User Controller signUp");
        logger.info("User: " + user);

        int isSignUp = userService.insertUser(user); // userService를 통해 사용자 등록 로직을 처리합니다.

        if (isSignUp == 1) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    @GetMapping("/user/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        logger.info("User Controller logout");

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok("success");
    }

    @ResponseBody
    @PostMapping("/user/delete")
    public Map<String, Object> deleteUser(@RequestBody Map<String, Object> requestData) {
        logger.info("User Controller delete user");

        Map<String, Object> response = new HashMap<>();

        int userID = Integer.parseInt(String.valueOf(requestData.get("userID")));

        System.out.println("userID: " + userID);

        int initSuccess = fileService.initFileAndFolder(userID);

        if (initSuccess == 1) {
            int deleteUserSuccesses = userService.deleteUser(userID);

            if (deleteUserSuccesses == 1) {
                response.put("status", "success");
                response.put("message", "delete user 성공");
            } else {
                response.put("status", "fail");
                response.put("message", "delete user 실패");
            }
        } else {
            response.put("status", "fail");
            response.put("message", "delete user 실패");
        }

        return response;
    }

    @ResponseBody
    @PostMapping("/user/edit")
    public Map<String, Object> userEdit(@RequestBody Map<String, Object> requestData) {
        logger.info("User Controller editUser");

        Map<String, Object> response = new HashMap<>();

        User user = new User();
        user.setUserID(Integer.parseInt(String.valueOf(requestData.get("userID"))));
        user.setName((String) requestData.get("name"));
        user.setEmail((String) requestData.get("email"));
        user.setPassword((String) requestData.get("password"));
        user.setLevel(Integer.parseInt(String.valueOf(requestData.get("level"))));
        user.setStorageMaxSize(Long.parseLong(String.valueOf(requestData.get("storageMaxSize"))));

        logger.info(user);

        int editUserSuccesses = userService.editUser(user);

        if (editUserSuccesses == 1) {
            response.put("status", "success");
            response.put("message", "edit user 성공");
        } else {
            response.put("status", "fail");
            response.put("message", "edit user 실패");
        }

        return response;
    }

    @GetMapping("/user/generateToken")
    public ResponseEntity<Map<String, String>> generateToken(@RequestParam("email") String email) {
        logger.info("User Controller generateToken: " + email);
        int executed = userService.deleteToken(email);
        String token = userService.generatedToken(email);
        Map<String, String> responseMap = new HashMap<>();

        if (token == null) responseMap.put("successes", "error");
        else {
            responseMap.put("successes", "successes");
            responseMap.put("token", token);
        }

        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/user/getToken")
    public ResponseEntity<Map<String, String>> getToken(@RequestParam("email") String email) {
        logger.info("User Controller getToken: " + email);
        String token = userService.getToken(email);
        Map<String, String> responseMap = new HashMap<>();

        if (token == null) responseMap.put("successes", "error");
        else {
            responseMap.put("successes", "successes");
            responseMap.put("token", token);
        }

        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/user/deleteToken")
    public ResponseEntity<Map<String, String>> deleteToken(@RequestParam("email") String email) {
        logger.info("User Controller deleteToken: " + email);
        int excuted = userService.deleteToken(email);
        Map<String, String> responseMap = new HashMap<>();

        if (excuted == 0) responseMap.put("successes", "error");
        else {
            responseMap.put("successes", "successes");
        }

        return ResponseEntity.ok(responseMap);
    }

    @ResponseBody
    @PostMapping("/user/isEmailAlready")
    public Map<String, Object> isEmailAlready(@RequestBody Map<String, Object> requestData) {
        String email = (String) requestData.get("email");
        logger.info("User Controller isEmailAlready: " + email);

        Map<String, Object> response = new HashMap<>();
        boolean check = false;

        if (email != null || !email.isEmpty()) check = userService.isEmailAlready(email);

        response.put("status", "success");
        response.put("check", check);

        return response;
    }
}
