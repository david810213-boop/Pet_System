package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.dto.CheckoutRequest;
import com.petgrooming.pet_system.enums.PaymentMethod;
import com.petgrooming.pet_system.model.User;
import com.petgrooming.pet_system.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentMvcController {

    private final PaymentService paymentService;
    // 從 session 取出登入的使用者，沒有就回傳 null
    private User getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute("loginUser");
    }
    // 列出付款紀錄（員工/管理員看全部，顧客只看自己的）
    @GetMapping
    public String list(HttpServletRequest request, Model model) {
        User user = getLoginUser(request);
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("user", user);
        if (user.isStaffOrAdmin()) {
            model.addAttribute("transactions", paymentService.getAllTransactions());
        } else {
            model.addAttribute("transactions",
                    paymentService.getMyTransactions(user.getUsername()));
        }
        return "payments/list";
    }
    // 結帳頁面（從預約列表點「結帳」連過來）
    @GetMapping("/checkout/{appointmentId}")
    public String checkoutPage(@PathVariable Long appointmentId,
                               HttpServletRequest request, Model model) {
        User user = getLoginUser(request);
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("user", user);
        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("checkoutRequest", new CheckoutRequest());
        return "payments/checkout";
    }
    // 處理結帳表單提交
    @PostMapping("/checkout/{appointmentId}/submit")
    public String checkoutSubmit(@PathVariable Long appointmentId,
                                 @ModelAttribute CheckoutRequest req,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        User user = getLoginUser(request);
        if (user == null) return "redirect:/auth/login";

        try {
            paymentService.checkout(appointmentId, req, user.getUsername());
            redirectAttributes.addFlashAttribute("successMsg", "結帳成功！");
            return "redirect:/payments";
        } catch (IllegalArgumentException e) {
            model.addAttribute("user", user);
            model.addAttribute("appointmentId", appointmentId);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("errorMsg", e.getMessage());
            return "payments/checkout";
        }
    }
}

