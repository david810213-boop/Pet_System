package com.petgrooming.pet_system.interceptor;

import com.petgrooming.pet_system.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {

        log.debug("LoginInterceptor 處理請求: {}", request.getRequestURI());
        String token = null;

        // 1. 優先從 Authorization Header 讀取
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 2. 找不到再從 Cookie 中尋找 JWT_TOKEN (Thymeleaf 直接轉跳網頁時最安全)
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("JWT_TOKEN".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        // 3. 驗證與解析 JWT
        if (token != null) {
            Claims claims = jwtUtils.parseToken(token);
            if (claims != null) {
                request.setAttribute("tokenUsername", claims.getSubject());
                request.setAttribute("tokenRole", claims.get("role", String.class));
                request.setAttribute("tokenSource", claims.get("source", String.class));
                return true; 
            }
        }

        // 4. 驗證失敗處理
        String uri = request.getRequestURI();
        log.debug("JWT 驗證失敗或未登入，來源路徑: {}", uri);

        if (uri.startsWith("/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"UNAUTHORIZED\",\"message\":\"請先登入\"}");
            return false;
        }

        response.sendRedirect("/auth/login?redirect=" + uri);
        return false;
    }
}
