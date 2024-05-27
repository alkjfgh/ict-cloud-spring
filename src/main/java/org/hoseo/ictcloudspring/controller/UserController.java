package org.hoseo.ictcloudspring.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hoseo.ictcloudspring.dao.UserService;
import org.hoseo.ictcloudspring.dto.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/user")
    public String userHome() {
        System.out.println("UserController Home");

        return "redirect:user/account";
    }

    @RequestMapping("/user/info")
    public String userInfo() {
        System.out.println("UserController info");

        return "user/userInfo";
    }

    @GetMapping("/user/account")
    public String isSignInSession(HttpServletRequest request) {
        System.out.println("UserController user session check");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) return "redirect:../file/upload";

        return "user/account";
    }

    @ResponseBody
    @PostMapping("/user/signIn")
    public Map<String, Object> signIn(@ModelAttribute User user, HttpServletRequest request) throws UnsupportedEncodingException {
        System.out.println("User Controller signIn");

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
        System.out.println("User Controller signUp");
        System.out.println("User: " + user);

        int isSignUp = userService.insertUser(user); // userService를 통해 사용자 등록 로직을 처리합니다.

        if (isSignUp == 1) {
            return ResponseEntity.ok("success");

//            mav.addObject("message", "회원가입 성공" + user);
//            mav.addObject("extraMessage", "기본 root 파일을 생성합니다.");
//            mav.setViewName("redirect:user/account"); // 회원가입 성공 시 보여줄 뷰의 이름
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    @GetMapping("/user/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        System.out.println("User Controller logout");

        HttpSession session = request.getSession();
        session.removeAttribute("user");

        return ResponseEntity.ok("success");
    }

    @GetMapping("/user/generateToken")
    public ResponseEntity<Map<String, String>> generateToken(@RequestParam("email") String email) {
        System.out.println("User Controller generateToken");
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
        System.out.println("User Controller getToken");
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
        System.out.println("User Controller deleteToken");
        int excuted = userService.deleteToken(email);
        Map<String, String> responseMap = new HashMap<>();

        if (excuted == 0) responseMap.put("successes", "error");
        else {
            responseMap.put("successes", "successes");
        }

        return ResponseEntity.ok(responseMap);
    }
}
