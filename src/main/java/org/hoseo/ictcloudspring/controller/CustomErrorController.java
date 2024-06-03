package org.hoseo.ictcloudspring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class CustomErrorController {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handle404(NoHandlerFoundException ex, Model model) {
        model.addAttribute("error", "404 Not Found");
        model.addAttribute("message", "The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.");
        return "error/404";
    }

    // 다른 예외 처리 핸들러를 추가할 수 있습니다.
}