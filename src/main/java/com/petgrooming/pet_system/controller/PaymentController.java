package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.annotation.RequireRole;
import com.petgrooming.pet_system.dto.CheckoutRequest;
import com.petgrooming.pet_system.dto.FinancialReportResponse;
import com.petgrooming.pet_system.dto.TransactionResponse;
import com.petgrooming.pet_system.enums.UserRole;
import com.petgrooming.pet_system.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 從 LoginInterceptor 解析 JWT 後存入的 request attribute 取得目前登入者
    private String currentUsername(HttpServletRequest request) {
        return (String) request.getAttribute("tokenUsername");
    }

    // ── POST /api/payments/{appointmentId}/checkout ────────────────────────
    // 結帳（對應原本 processPayment），appointmentId = 要結哪一筆預約的帳
    @PostMapping("/{appointmentId}/checkout")
    public ResponseEntity<?> checkout(
            @PathVariable Long appointmentId,
            @RequestBody CheckoutRequest req,
            HttpServletRequest request) {
        try {
            TransactionResponse res = paymentService.checkout(appointmentId, req, currentUsername(request));
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── GET /api/payments/my ───────────────────────────────────────────────
    // 查詢自己的交易紀錄（對應原本 queryTransactions）
    @GetMapping("/my")
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.getMyTransactions(currentUsername(request)));
    }

    // ── GET /api/payments/transactions ────────────────────────────────────
    // 查詢所有交易（STAFF / ADMIN）
    @RequireRole({UserRole.ADMIN, UserRole.STAFF})
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(paymentService.getAllTransactions());
    }

    // ── GET /api/payments/report ───────────────────────────────────────────
    // 財務報告（ADMIN，對應原本 generateFinancialReport）
    @RequireRole(UserRole.ADMIN)
    @GetMapping("/report")
    public ResponseEntity<FinancialReportResponse> getFinancialReport() {
        return ResponseEntity.ok(paymentService.getFinancialReport());
    }
}
