package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.dto.LoginRequest;
import com.petgrooming.pet_system.dto.RegisterRequest;
import com.petgrooming.pet_system.model.User;
import com.petgrooming.pet_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthMvcController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String redirect,
                            @RequestParam(required = false) String error,
                            Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        model.addAttribute("redirect", redirect);
        if (error != null) {
            model.addAttribute("errorMsg", "帳號或密碼錯誤，請重新輸入");
        }
        return "auth/login";
    }

    @PostMapping("/login/submit")
    public String loginSubmit(@Valid @ModelAttribute LoginRequest req,
                              BindingResult bindingResult,
                              @RequestParam(required = false) String redirect,
                              HttpSession session,
                              Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("redirect", redirect);
            return "auth/login";
        }

        Optional<User> userOpt = userService.authenticate(req.getUsername(), req.getPassword());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("loginUser", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole().name());

            if (redirect != null && !redirect.isBlank() && !redirect.startsWith("/auth")) {
                return "redirect:" + redirect;
            }
            return "redirect:/dashboard";

        } else {
            return "redirect:/auth/login?error=true" +
                    (redirect != null ? "&redirect=" + redirect : "");
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register/submit")
    public String registerSubmit(@Valid @ModelAttribute RegisterRequest req,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("registerRequest", req);
            return "auth/register";
        }

        try {
            userService.register(req);
            // BUG 4 修正：key 改為 successMsg，與 login.html 的 ${successMsg} 一致
            redirectAttributes.addFlashAttribute("successMsg", "註冊成功！請登入");
            return "redirect:/auth/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("registerRequest", req);
            model.addAttribute("errorMsg", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
