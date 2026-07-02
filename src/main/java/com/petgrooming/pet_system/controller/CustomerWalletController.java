package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.dto.WalletResponse;
import com.petgrooming.pet_system.dto.WalletTransactionResponse;
import com.petgrooming.pet_system.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 顧客端錢包 API（LIFF 呼叫）
 * 顧客只能查詢自己的餘額和紀錄，無法自己儲值
 * 儲值由店家後台操作（CustomerWalletAdminController）
 */
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class CustomerWalletController {

    private final WalletService walletService;

    private String currentUsername(HttpServletRequest request) {
        return (String) request.getAttribute("tokenUsername");
    }

    // ── GET /api/wallet ────────────────────────────────────────────────────
    // 查詢自己的錢包（餘額、會員卡等級、折扣）
    @GetMapping
    public ResponseEntity<WalletResponse> getMyWallet(HttpServletRequest request) {
        return ResponseEntity.ok(walletService.getWallet(currentUsername(request)));
    }

    // ── GET /api/wallet/transactions ───────────────────────────────────────
    // 查詢自己的儲值/消費紀錄
    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransactionResponse>> getMyTransactions(HttpServletRequest request) {
        return ResponseEntity.ok(walletService.getTransactions(currentUsername(request)));
    }
}
