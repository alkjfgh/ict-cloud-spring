package org.hoseo.ictcloudspring.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    private static final Logger logger = LogManager.getLogger(MainController.class);

    @RequestMapping("/")
    public String home() {
        logger.info("Main Controller home");
        return "redirect:/main";
    }

    @GetMapping("/main")
    protected String intoMain() {
        logger.info("Main Controller into main");
        return "main";
    }
}
