package com.petgrooming.pet_system.dto;

import com.petgrooming.pet_system.enums.MemberCardTier;
import com.petgrooming.pet_system.model.Wallet;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class WalletResponse {
    private Integer balance;
    private MemberCardTier cardTier;
    private String cardTierLabel;
    private double discount;
    private boolean cardActive;
    private LocalDate cardActivatedAt;
    private LocalDate cardExpiresAt;
    private boolean productPrice;

    public static WalletResponse from(Wallet w) {
        return WalletResponse.builder()
                .balance(w.getBalance())
                .cardTier(w.getCardTier())
                .cardTierLabel(w.getCardTier().getLabel())
                .discount(w.getEffectiveDiscount())
                .cardActive(w.isCardActive())
                .cardActivatedAt(w.getCardActivatedAt())
                .cardExpiresAt(w.getCardExpiresAt())
                .productPrice(w.getCardTier().isProductPrice())
                .build();
    }
}
