package org.hoseo.ictcloudspring.common;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.controller.UserController;
import org.hoseo.ictcloudspring.dto.User;

import java.io.IOException;

public class LoginFilter implements Filter {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("check user session filter");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);

        // 로그인 페이지와 리소스 요청을 제외한 나머지 요청에 대해 세션 체크
        String uri = req.getRequestURI();
        logger.info("uri: " + uri);

        if (session == null || session.getAttribute("user") == null) {
            if (!uri.endsWith("account") && !uri.endsWith("main") && uri.endsWith(".css") && uri.endsWith(".js") && !uri.endsWith(".jpg") && !uri.endsWith(".png")) {
                res.sendRedirect(req.getContextPath() + "/user/account");
                return;
            }
        } /*else {
            System.out.println("================================");
            System.out.println("================================");
            System.out.println("else!!!!!!!!!!!!!!!!!!!!");
            User user = (User) session.getAttribute("user");
            System.out.println(user);
            if (user.getLevel() != 2 && !uri.endsWith("admin")) {
                logger.warn("unauthorized enter admin page. User: " + user);
                System.out.print("path: ");
                System.out.println(req.getContextPath() + "/main");
                res.sendRedirect(req.getContextPath() + "/main");
                return;
            }
        }*/

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
