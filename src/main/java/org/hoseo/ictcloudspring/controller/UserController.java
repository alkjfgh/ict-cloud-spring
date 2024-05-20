package org.hoseo.ictcloudspring.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hoseo.ictcloudspring.dao.UserService;
import org.hoseo.ictcloudspring.dto.User;

import org.springframework.beans.factory.annotation.Autowired;
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
        System.out.println("SignIn Controller signIn");

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
    public ModelAndView signUp(@ModelAttribute User user) {
        System.out.println("SignIn Controller signUp");
        System.out.println("User: " + user);

        ModelAndView mav = new ModelAndView();

        int isSignUp = userService.insertUser(user); // userService를 통해 사용자 등록 로직을 처리합니다.

        if (isSignUp == 1) {
            mav.addObject("message", "회원가입 성공" + user);
            mav.addObject("extraMessage", "기본 root 파일을 생성합니다.");
            mav.setViewName("redirect:user/account"); // 회원가입 성공 시 보여줄 뷰의 이름
        } else {
            mav.addObject("message", "회원가입 실패" + user);
            mav.setViewName("redirect:failurePage"); // 회원가입 실패 시 보여줄 뷰의 이름
        }

        return mav;

        // TODO 페이지 이동 처리 해야함 signin 처럼 js에서
    }
}
