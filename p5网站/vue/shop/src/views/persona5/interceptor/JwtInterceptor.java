package com.guanyanliang.persona5.interceptor;

import com.guanyanliang.persona5.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();

        // 登录注册接口不拦截
        if (uri.equals("/admin/login")
                || uri.startsWith("/api/user/login")
                || uri.startsWith("/api/user/register")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("未提供 token");
            return false;
        }

        String token = authHeader.replace("Bearer ", "").trim();

        try {

            // 获取用户名
            String username = JwtUtil.getUsername(token);

            // 获取角色
            String role = JwtUtil.getRole(token);

            // 存入 request（后面 Controller 可以用）
            request.setAttribute("username", username);
            request.setAttribute("role", role);

            // 管理员权限判断
            if (uri.startsWith("/admin")
                    && !( "ADMIN".equals(role) || "SUPERADMIN".equals(role) )) {

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("权限不足");
                return false;
            }

            return true;

        } catch (Exception e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("无效 token");
            return false;

        }
    }
}