package com.cjy.interceptors;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.cjy.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // ==================== 1. 重点放行登录、注册和静态资源 ====================
        if (
            // 登录注册接口（精确匹配 + 带斜杠保险）
                "/user/login".equals(uri) ||
                        "/user/login/".equals(uri) ||
                        "/user/register".equals(uri) ||
                        "/user/register/".equals(uri) ||

                        // 页面
                        uri.contains(".html") ||
                        uri.contains(".js") ||
                        uri.contains(".css") ||
                        "/".equals(uri) ||
                        "/index.html".equals(uri) ||
                        "/main.html".equals(uri) ||

                        // 静态资源
                        uri.startsWith("/css/") ||
                        uri.startsWith("/js/") ||
                        uri.startsWith("/img/") ||
                        uri.startsWith("/images/") ||
                        uri.startsWith("/BootStrap/") ||
                        uri.startsWith("/fonts/") ||
                        uri.startsWith("/iconfont/") ||
                        uri.startsWith("/content/") ||

                        // 常见静态后缀
                        uri.endsWith(".css") || uri.endsWith(".js") ||
                        uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".jpeg") ||
                        uri.endsWith(".ico") || uri.endsWith(".ttf") || uri.endsWith(".woff") || uri.endsWith(".woff2")
        ) {
            return true;  // 直接放行
        }

        // ==================== 2. 放行 OPTIONS 预检请求（前后端分离必备）==================
        if ("OPTIONS".equalsIgnoreCase(method)) {
            response.setStatus(200);
            return true;
        }

        // ==================== 3. 检查 token ====================
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            sendError(response, "未登录");
            return false;
        }

        String token = auth.substring(7);

        try {
            JwtUtil.parseToken(token);  // 使用你的 parseToken 方法验证
            return true;
        } catch (JWTVerificationException e) {
            sendError(response, "token无效或已过期");
            return false;
        }
    }

    private void sendError(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("{\"code\":401,\"msg\":\"" + msg + "\"}");
        writer.flush();
    }
}