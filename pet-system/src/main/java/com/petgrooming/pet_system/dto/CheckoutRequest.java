package com.petgrooming.pet_system.dto;

import com.petgrooming.pet_system.enums.PaymentMethod;
import lombok.Data;

// 前端傳來的結帳請求（對應原本 processPayment 的 Scanner 輸入）
@Data
public class CheckoutRequest {
    private PaymentMethod paymentMethod;    // CASH / CREDIT_CARD / LINE_PAY
}