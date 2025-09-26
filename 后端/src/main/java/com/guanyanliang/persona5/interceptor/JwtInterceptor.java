package com.guanyanliang.persona5.interceptor;

import com.guanyanliang.persona5.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 登录接口不拦截
        if (uri.equals("/admin/login")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("未提供 token");
            return false;
        }

        String token = authHeader.replace("Bearer ", "").trim();
        String role;
        try {
            role = JwtUtil.getRole(token);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("无效 token");
            return false;
        }

        // 管理员接口权限校验
        if (uri.startsWith("/admin") && !"admin".equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("权限不足");
            return false;
        }

        return true;
    }

}
