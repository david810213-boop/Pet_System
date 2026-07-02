package com.petgrooming.pet_system.model;

import com.petgrooming.pet_system.enums.WalletTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 錢包異動紀錄
 * 每次儲值、消費扣款、退款都記一筆，提供完整歷程查詢
 */
@Entity
@Table(name = "wallet_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    @ToString.Exclude
    private Wallet wallet;

    // 異動類型
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletTransactionType type;

    // 異動金額（正數 = 加錢，負數 = 扣錢）
    @Column(nullable = false)
    private Integer amount;

    // 異動後餘額（快照，方便顯示歷程不用重算）
    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter;

    // 備註（例如：「普卡儲值」「預約#5 消費扣款」）
    @Column(length = 200)
    private String note;

    // 關聯的預約 ID（消費扣款時使用，選填）
    @Column(name = "appointment_id")
    private Long appointmentId;

    // 異動時間
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
