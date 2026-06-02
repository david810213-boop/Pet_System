package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.dto.CreateStaffRequest;
import com.petgrooming.pet_system.dto.UserResponse;
import com.petgrooming.pet_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// UserController 只保留「需要登入後」才能使用的端點
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ── GET /api/users/me ──────────────────────────────────────────────────
    // 查詢自己的資料（需帶 X-Username）
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader("X-Username") String username) {
        try {
            UserResponse res = userService.getMe(username);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── GET /api/users ─────────────────────────────────────────────────────
    // 查詢所有使用者（ADMIN 限定）
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ── GET /api/users/staff ───────────────────────────────────────────────
    // 查詢所有員工清單（ADMIN 限定）
    @GetMapping("/staff")
    public ResponseEntity<List<UserResponse>> getAllStaff() {
        return ResponseEntity.ok(userService.getAllStaff());
    }

    // ── POST /api/users/staff ──────────────────────────────────────────────
    // 新增員工帳號（ADMIN 限定）
    @PostMapping("/staff")
    public ResponseEntity<?> createStaff(@Valid @RequestBody CreateStaffRequest req) {
        try {
            UserResponse res = userService.createStaff(req);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
