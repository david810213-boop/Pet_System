
package com.petgrooming.pet_system.enums;

import lombok.Getter;

/**
 * 慕沐村會員卡等級
 * 依單筆儲值金額自動升級，有效期限為開卡後 365 天
 */
@Getter
public enum MemberCardTier {

    NONE(0, 1.00, false, "無"),
    VILLAGE(5000, 1.00, false, "村民優惠方案"), // 贈 200 元，無折扣
    NORMAL(8000, 0.95, true, "普卡"),
    GOLD(15000, 0.90, true, "金卡"),
    DIAMOND(30000, 0.88, true, "鑽石卡"),
    VIP(50000, 0.85, true, "慕沐村VIP");

    private final int minAmount; // 達到此等級所需的單筆儲值金額（元）
    private final double discount; // 服務折扣率（1.0 = 無折扣）
    private final boolean productPrice; // 是否享有商品會員價
    private final String label; // 顯示名稱

    MemberCardTier(int minAmount, double discount, boolean productPrice, String label) {
        this.minAmount = minAmount;
        this.discount = discount;
        this.productPrice = productPrice;
        this.label = label;
    }

    /**
     * 依單筆儲值金額判斷升級到哪個等級
     * 由高到低比對，回傳第一個符合的等級
     */
    public static MemberCardTier fromAmount(int amount) {
        MemberCardTier[] tiers = values();
        for (int i = tiers.length - 1; i >= 0; i--) {
            if (amount >= tiers[i].minAmount) {
                return tiers[i];
            }
        }
        return NONE;
    }
}
