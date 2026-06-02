package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.dto.CheckoutRequest;
import com.petgrooming.pet_system.dto.FinancialReportResponse;
import com.petgrooming.pet_system.dto.TransactionResponse;
import com.petgrooming.pet_system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ── POST /api/payments/{appointmentId}/checkout ────────────────────────
    // 結帳（對應原本 processPayment）
    // appointmentId = 要結哪一筆預約的帳
    // X-Username    = 目前登入的使用者（之後換成 Spring Security）
    @PostMapping("/{appointmentId}/checkout")
    public ResponseEntity<?> checkout(
            @PathVariable Long appointmentId,
            @RequestBody CheckoutRequest req,
            @RequestHeader("X-Username") String username) {
        try {
            TransactionResponse res = paymentService.checkout(appointmentId, req, username);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── GET /api/payments/my ───────────────────────────────────────────────
    // 查詢自己的交易紀錄（對應原本 queryTransactions）
    @GetMapping("/my")
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(
            @RequestHeader("X-Username") String username) {
        return ResponseEntity.ok(paymentService.getMyTransactions(username));
    }

    // ── GET /api/payments/transactions ────────────────────────────────────
    // 查詢所有交易（STAFF / ADMIN）
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(paymentService.getAllTransactions());
    }

    // ── GET /api/payments/report ───────────────────────────────────────────
    // 財務報告（ADMIN，對應原本 generateFinancialReport）
    @GetMapping("/report")
    public ResponseEntity<FinancialReportResponse> getFinancialReport() {
        return ResponseEntity.ok(paymentService.getFinancialReport());
    }
}
