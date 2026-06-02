package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.annotation.RequireRole;
import com.petgrooming.pet_system.dto.AppointmentRequest;
import com.petgrooming.pet_system.dto.GroomingItemResponse;
import com.petgrooming.pet_system.enums.UserRole;
import com.petgrooming.pet_system.model.User;
import com.petgrooming.pet_system.service.AppointmentService;
import com.petgrooming.pet_system.service.interfaces.GroomingService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentMvcController {

    private final AppointmentService appointmentService;
    private final GroomingService groomingItemService; 

    // ── GET /appointments ──────────────────────────────────────────────────
    // 💡 作用：查看預約清單。不貼貼紙，因為一般會員與員工登入後都能看（各自看不同範圍）
    @GetMapping
    public String list(HttpSession session, Model model) {
        // 🛡️ 攔截器已經保證絕對有登入，直接從 session 拿，100% 放心安全！
        User user = (User) session.getAttribute("loginUser");
        
        model.addAttribute("user", user);
        if (user.isStaffOrAdmin()) {
            model.addAttribute("appointments", appointmentService.getAllAppointments());
        } else {
            model.addAttribute("appointments", appointmentService.getMyAppointments(user.getUsername()));
        }
        return "appointments/list";
    }

    // ── GET /appointments/new ──────────────────────────────────────────────
    // 💡 作用：開啟預約表單。同樣只需登入，不限制特定角色。
    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");

        var myPets = appointmentService.getMyPetsForBooking(user.getUsername());
        List<GroomingItemResponse> availableServices = groomingItemService.getAllItems();

        model.addAttribute("user", user);
        model.addAttribute("myPets", myPets);
        model.addAttribute("appointmentRequest", new AppointmentRequest());
        model.addAttribute("groomingItems", availableServices); 
        return "appointments/form";
    }

    // ── GET /appointments/slots ────────────────────────────────────────────
    // 💡 作用：查詢某日期的空檔。
    @GetMapping("/slots")
    public String slots(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                        HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        
        model.addAttribute("user", user);
        model.addAttribute("slots", appointmentService.getAvailableSlots(date));
        model.addAttribute("selectedDate", date);
        return "appointments/slots";
    }

    // ── POST /appointments/submit ──────────────────────────────────────────
    // 💡 作用：送出預約表單。
    @PostMapping("/submit")
    public String submit(@Valid @ModelAttribute AppointmentRequest req,
                         BindingResult bindingResult,
                         HttpSession session,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        User user = (User) session.getAttribute("loginUser");

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("myPets", appointmentService.getMyPetsForBooking(user.getUsername()));
            model.addAttribute("groomingItems", groomingItemService.getAllItems()); 
            return "appointments/form";
        }

        try {
            appointmentService.book(req, user.getUsername());
            redirectAttributes.addFlashAttribute("successMsg", "預約成功！");
            return "redirect:/appointments";

        } catch (IllegalArgumentException e) {
            model.addAttribute("user", user);
            model.addAttribute("myPets", appointmentService.getMyPetsForBooking(user.getUsername()));
            model.addAttribute("groomingItems", groomingItemService.getAllItems());
            model.addAttribute("errorMsg", e.getMessage());
            return "appointments/form";
        }
    }

    // ── 💡 額外加碼練習：管理員專用後台 ─────────────────────────────────────────
    // 🛡️ 啪！貼上你的自訂防偽貼紙。一般會員（CUSTOMER）如果敢打這個網址，直接在 RoleInterceptor 就會被彈飛！
    @RequireRole({UserRole.ADMIN, UserRole.STAFF})
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        // 這裡不需要從 session 撈 user 來檢查了，因為警衛已經幫你嚴格把關！
        return "appointments/admin_dashboard";
    }
}