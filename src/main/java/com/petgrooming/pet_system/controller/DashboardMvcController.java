package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.model.User;
import com.petgrooming.pet_system.service.AppointmentService;
import com.petgrooming.pet_system.service.PetService;
import com.petgrooming.pet_system.service.UserService; // 1. 引入 UserService
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardMvcController {

    private final AppointmentService appointmentService;
    private final PetService petService;
    private final UserService userService; // 2. 注入 UserService 用於藉由帳號撈取完整資料

    /**
     * JWT 版獲取當前登入使用者
     */
    /**
     * JWT 版獲取當前登入使用者
     */
    private User getLoginUser(HttpServletRequest request) {
        // 從 LoginInterceptor 存入的 request attribute 拿取 username
        String username = (String) request.getAttribute("tokenUsername");
        if (username == null)
            return null;

        try {
            // 🎯 直接呼叫你原本就寫好的 getUserEntityByUsername 方法！
            return userService.getUserEntityByUsername(username);
        } catch (IllegalArgumentException e) {
            // 如果拋出找不到使用者的異常，就回傳 null
            return null;
        }
    }

    @GetMapping
    public String dashboard(HttpServletRequest request, Model model) {
        User user = getLoginUser(request);
        if (user == null) {
            return "redirect:/auth/login";
        }

        // 將用戶物件塞給前端 Thymeleaf 渲染網頁畫面
        model.addAttribute("user", user);

        if (user.isStaffOrAdmin()) {
            model.addAttribute("appointments", appointmentService.getAllAppointments());
        } else {
            model.addAttribute("appointments",
                    appointmentService.getMyAppointments(user.getUsername()));
            model.addAttribute("pets",
                    petService.getMyPets(user.getUsername()));
        }
        return "dashboard";
    }
}