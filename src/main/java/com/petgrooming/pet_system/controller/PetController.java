package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.dto.PetRequest;
import com.petgrooming.pet_system.dto.PetResponse;
import com.petgrooming.pet_system.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    // ── POST /api/pets ─────────────────────────────────────────────────────
    // 新增寵物，以 X-Username 識別飼主（之後換成 Spring Security token）
    // @Valid 觸發 PetRequest 的 Bean Validation，驗證失敗自動回 400
    @PostMapping
    public ResponseEntity<?> addPet(
            @RequestHeader("X-Username") String username,
            @Valid @RequestBody PetRequest request) {
        try {
            PetResponse res = petService.addPet(username, request);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── GET /api/pets/my ───────────────────────────────────────────────────
    @GetMapping("/my")
    public ResponseEntity<?> getMyPets(
            @RequestHeader("X-Username") String username) {
        try {
            List<PetResponse> res = petService.getMyPets(username);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

