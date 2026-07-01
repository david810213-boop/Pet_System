package com.petgrooming.pet_system.enums;

import lombok.Getter;

@Getter
public enum WalletTransactionType {
    DEPOSIT("儲值"), // 顧客儲值（加錢）
    DEPOSIT_BONUS("儲值贈點"), // 村民方案贈送的 200 元
    DEDUCT("消費扣款"), // 結帳時扣款
    REFUND("退款"); // 退款

    private final String label;

    WalletTransactionType(String label) {
        this.label = label;
    }
}
