package org.hoseo.ictcloudspring.common;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("check user session  filter");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // 로그인 페이지와 리소스 요청을 제외한 나머지 요청에 대해 세션 체크
        String uri = req.getRequestURI();
        System.out.println(uri);
        if (session == null || session.getAttribute("user") == null) {
            if (!uri.endsWith("account") && !uri.endsWith("main") && uri.endsWith(".css") && uri.endsWith(".js")) {
                res.sendRedirect(req.getContextPath() + "/user/account");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
