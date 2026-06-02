package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.dto.PetRequest;
import com.petgrooming.pet_system.enums.PetType;
import com.petgrooming.pet_system.model.User;
import com.petgrooming.pet_system.service.PetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetMvcController {

    private final PetService petService;

    private User getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute("loginUser");
    }

    @GetMapping
    public String list(HttpServletRequest request, Model model) {
        User user = getLoginUser(request);
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("user", user);
        model.addAttribute("pets", petService.getMyPets(user.getUsername()));
        return "pets/list";
    }

    @GetMapping("/new")
    public String newForm(HttpServletRequest request, Model model) {
        User user = getLoginUser(request);
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("user", user);
        model.addAttribute("petRequest", new PetRequest());
        model.addAttribute("petTypes", PetType.values());
        return "pets/form";
    }

    @PostMapping("/submit")
    public String submit(@Valid @ModelAttribute PetRequest req,
                         BindingResult bindingResult,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        User user = getLoginUser(request);
        if (user == null) return "redirect:/auth/login";

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("petTypes", PetType.values());
            return "pets/form";
        }

        try {
            petService.addPet(user.getUsername(), req);
            redirectAttributes.addFlashAttribute("successMsg", "寵物新增成功！");
            return "redirect:/pets";
        } catch (IllegalArgumentException e) {
            model.addAttribute("user", user);
            model.addAttribute("petTypes", PetType.values());
            model.addAttribute("errorMsg", e.getMessage());
            return "pets/form";
        }
    }
}
