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

/**
 * 登入驗證攔截器（JWT 版）
 * 從 Cookie 獲取 JWT_TOKEN 並驗證
 */
@Component
@Slf4j
@RequiredArgsConstructor // 注入工具類別
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {

        log.debug("LoginInterceptor 處理請求: {}", request.getRequestURI());

        String token = null;

        // 1. 從 Cookies 中尋找名為 JWT_TOKEN 的 Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 2. 如果有拿到 Token，嘗試解析它
        if (token != null) {
            Claims claims = jwtUtils.parseToken(token);

            if (claims != null) {
                // 驗證成功！將用戶資訊暫存到 request 中，傳給後續的 RoleInterceptor 或 Controller 使用
                request.setAttribute("tokenUsername", claims.getSubject());
                request.setAttribute("tokenRole", claims.get("role", String.class));
                return true; // 放行
            }
        }

        // 3. 未登入或 Token 驗證失敗：記住原始路徑，導回登入頁
        String uri = request.getRequestURI();
        log.debug("JWT 驗證失敗或未登入，redirect 到登入頁，來源路徑: {}", uri);
        response.sendRedirect("/auth/login?redirect=" + uri);
        return false;
    }
}
