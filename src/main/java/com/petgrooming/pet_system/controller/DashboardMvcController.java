package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.model.User;
import com.petgrooming.pet_system.service.AppointmentService;
import com.petgrooming.pet_system.service.PetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardMvcController {

    private final AppointmentService appointmentService;
    private final PetService petService;

    private User getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute("loginUser");
    }

    @GetMapping
    public String dashboard(HttpServletRequest request, Model model) {
        User user = getLoginUser(request);
        if (user == null) return "redirect:/auth/login";

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