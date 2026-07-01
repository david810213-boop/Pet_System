package com.petgrooming.pet_system.dto;

import com.petgrooming.pet_system.enums.WalletTransactionType;
import com.petgrooming.pet_system.model.WalletTransaction;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WalletTransactionResponse {
    private Long id;
    private WalletTransactionType type;
    private String typeLabel;
    private Integer amount;
    private Integer balanceAfter;
    private String note;
    private Long appointmentId;
    private LocalDateTime createdAt;

    public static WalletTransactionResponse from(WalletTransaction t) {
        return WalletTransactionResponse.builder()
                .id(t.getId())
                .type(t.getType())
                .typeLabel(t.getType().getLabel())
                .amount(t.getAmount())
                .balanceAfter(t.getBalanceAfter())
                .note(t.getNote())
                .appointmentId(t.getAppointmentId())
                .createdAt(t.getCreatedAt())
                .build();
    }
}