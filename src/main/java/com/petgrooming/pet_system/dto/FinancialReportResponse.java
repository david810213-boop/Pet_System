package com.petgrooming.pet_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// 財務報告（對應原本 generateFinancialReport 印出的格式）
@Data
@AllArgsConstructor
public class FinancialReportResponse {
    private LocalDateTime generatedAt;      // 報告產出時間
    private int totalTransactions;          // 總交易筆數
    private int paidCount;                  // 已付款筆數
    private double totalRevenue;            // 總營收
    private double averageAmount;           // 平均客單價
    private List<TransactionResponse> details; // 明細清單
}