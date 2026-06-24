package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.dto.LoginRequest;
import com.petgrooming.pet_system.dto.RegisterRequest;
import com.petgrooming.pet_system.model.User;
import com.petgrooming.pet_system.service.UserService;
import com.petgrooming.pet_system.utils.JwtUtils; // 1. 引入剛剛修好的 JwtUtils
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    private final JwtUtils jwtUtils; // 2. 注入 JwtUtils

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
            HttpServletResponse response, // 3. 換成 HttpServletResponse 來塞 Cookie
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("redirect", redirect);
            return "auth/login";
        }

        Optional<User> userOpt = userService.authenticate(req.getUsername(), req.getPassword());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 4. 關鍵核心：生成 JWT Token
            String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());

            // 5. 將 Token 包進 Cookie 中送給瀏覽器
            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
            jwtCookie.setHttpOnly(true); // 防止前端 JavaScript 竊取，防範 XSS 攻擊
            jwtCookie.setPath("/"); // 整個專案路徑都有效
            jwtCookie.setMaxAge(86400); // 有效期設為 1 天（單位：秒，與 Token 的 24 小時同步）

            response.addCookie(jwtCookie); // 真正寫入瀏覽器

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
            redirectAttributes.addFlashAttribute("successMsg", "註冊成功！請登入");
            return "redirect:/auth/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("registerRequest", req);
            model.addAttribute("errorMsg", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // 6. 登出的做法：弄一個同名、時效為 0 的 Cookie 覆蓋過去，瀏覽器就會自動刪除它
        Cookie jwtCookie = new Cookie("JWT_TOKEN", null);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // 設為 0 代表立即失效
        response.addCookie(jwtCookie);

        return "redirect:/auth/login";
    }
}
