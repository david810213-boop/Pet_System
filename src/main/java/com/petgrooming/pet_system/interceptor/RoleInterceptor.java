package com.petgrooming.pet_system.interceptor;

import com.petgrooming.pet_system.annotation.RequireRole;
import com.petgrooming.pet_system.enums.UserRole;
import com.petgrooming.pet_system.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 角色權限攔截器 — 參考自 parking-system RoleInterceptor
 *
 * 運作方式：
 * 1. 只處理有 @RequireRole 的方法或類別
 * 2. 從 Session 取出 loginUser（User entity）
 * 3. 比對角色，不符合回 403，MVC 頁面 redirect 到 /dashboard
 */
@Component
@Slf4j
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {

        // 只處理 Controller 方法，靜態資源直接放行
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 1. 先查方法層級的 @RequireRole，再查類別層級
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        // 沒有注解，代表不需要特定角色，直接放行
        if (requireRole == null) {
            return true;
        }

        // 2. 從 Session 取登入使用者（Login 攔截器已確保不會是 null）
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("RoleInterceptor：Session 不存在，拒絕存取 {}", request.getRequestURI());
            response.sendRedirect("/auth/login");
            return false;
        }

        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            log.warn("RoleInterceptor：Session 無使用者，拒絕存取 {}", request.getRequestURI());
            response.sendRedirect("/auth/login");
            return false;
        }

        // 3. 比對角色是否符合
        UserRole[] required = requireRole.value();
        boolean hasPermission = Arrays.asList(required).contains(user.getRole());

        if (!hasPermission) {
            log.warn("RoleInterceptor：使用者 {} (角色: {}) 嘗試存取需要角色 {} 的路徑: {}",
                    user.getUsername(), user.getRole(),
                    Arrays.toString(required), request.getRequestURI());

            // MVC 應用：導回首頁並帶錯誤訊息，而非回 403 JSON
            response.sendRedirect("/dashboard?error=forbidden");
            return false;
        }

        log.debug("RoleInterceptor：使用者 {} 通過角色驗證，存取 {}", user.getUsername(), request.getRequestURI());
        return true;
    }
}
