package org.hoseo.ictcloudspring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    @RequestMapping("/")
    public String home() {
        System.out.println("home");
        return "redirect:/main";
    }

    @GetMapping("/main")
    protected String intoMain(Model model) {
        System.out.println("Main Controller into main ");

        return "main";
    }
}
