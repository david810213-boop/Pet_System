package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.dto.AppointmentRequest;
import com.petgrooming.pet_system.dto.AppointmentResponse;
import com.petgrooming.pet_system.dto.TimeSlotResponse;
import com.petgrooming.pet_system.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // ── POST /api/appointments ─────────────────────────────────────────────
    // 建立預約（對應原本 bookAppointment）
    // 目前先用 @RequestHeader 傳 username，之後加 Spring Security 再改成
    // @AuthenticationPrincipal UserDetails user
    @PostMapping
    public ResponseEntity<?> book(
            @RequestBody AppointmentRequest req,
            @RequestHeader("X-Username") String username) {
        try {
            AppointmentResponse res = appointmentService.book(req, username);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            // 時間超出營業時間、時段重疊等錯誤
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── GET /api/appointments/my ───────────────────────────────────────────
    // 查詢自己的預約紀錄（對應原本 viewAppointments）
    @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            @RequestHeader("X-Username") String username) {
        return ResponseEntity.ok(appointmentService.getMyAppointments(username));
    }

    // ── GET /api/appointments ──────────────────────────────────────────────
    // 查詢所有預約（STAFF / ADMIN，對應原本 viewAllAppointments）
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    // ── GET /api/appointments/slots?date=2025-06-01 ────────────────────────
    // 查詢某天可預約時段（對應原本 generateDailySlots + getAvailableSlots）
    @GetMapping("/slots")
    public ResponseEntity<List<TimeSlotResponse>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(date));
    }
}

