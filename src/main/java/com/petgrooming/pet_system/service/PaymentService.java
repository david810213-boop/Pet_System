package com.petgrooming.pet_system.service;

import com.petgrooming.pet_system.dto.CheckoutRequest;
import com.petgrooming.pet_system.dto.FinancialReportResponse;
import com.petgrooming.pet_system.dto.TransactionResponse;
import com.petgrooming.pet_system.model.Appointment;
import com.petgrooming.pet_system.model.Transaction;
import com.petgrooming.pet_system.model.User;
import com.petgrooming.pet_system.repository.AppointmentRepository;
import com.petgrooming.pet_system.repository.TransactionRepository;
import com.petgrooming.pet_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AppointmentRepository appointmentRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // ── 1. 結帳 ────────────────────────────────
    @Transactional  //確保完成交易跟建立交易紀錄是一併完成的
    public TransactionResponse checkout(Long appointmentId,
                                        CheckoutRequest req,
                                        String username) {
        // 1a. 確認預約存在
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("找不到該預約"));

        // 1b. 確認預約屬於此使用者（CUSTOMER 只能付自己的帳）
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者"));

        boolean isOwner = appointment.getUser().getId().equals(user.getId());
        boolean isStaffOrAdmin = user.getRole().name().equals("ADMIN")
                              || user.getRole().name().equals("STAFF");

        if (!isOwner && !isStaffOrAdmin) {
            throw new IllegalArgumentException("權限不足：只能結自己的帳");
        }

        // 1c. 確認尚未付款
        if (appointment.isPaid()) {
            throw new IllegalArgumentException("此預約已完成結帳");
        }

        // 1d. 確認沒有重複交易紀錄
        if (transactionRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new IllegalArgumentException("此預約已有交易紀錄");
        }

        // 1e. 計算最終金額（對應原本各 PaymentSystem.calculateTotal 策略）
        int baseAmount  = appointment.getTotalAmount();
        int finalAmount = req.getPaymentMethod().calculateFinalAmount(baseAmount);

        // 1f. 決定經手人（對應原本 staffInfo 判斷）
        String handledBy = isOwner
                ? "會員自助（" + user.getName() + "）"
                : "員工：" + user.getName();

        // 1g. 建立交易紀錄並儲存
        Transaction transaction = Transaction.builder()
                .appointment(appointment)
                .user(appointment.getUser())        // 交易歸屬預約的飼主
                .paymentMethod(req.getPaymentMethod())
                .baseAmount(baseAmount)
                .finalAmount(finalAmount)
                .paid(true)
                .paymentTime(LocalDateTime.now())
                .handledBy(handledBy)
                .build();

        transactionRepository.save(transaction);

        // 1h. 將預約標記為已付款
        appointment.setPaid(true);
        appointmentRepository.save(appointment);

        return TransactionResponse.from(transaction);
    }

    // ── 2. 查詢自己的交易紀錄（對應原本 queryTransactions）──────────────
    public List<TransactionResponse> getMyTransactions(String username) {
        return transactionRepository.findByUserUsername(username)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    // ── 3. 查詢所有交易（STAFF/ADMIN）────────────────────────────────────
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    // ── 4. 財務報告（ADMIN，對應原本 generateFinancialReport）────────────
    public FinancialReportResponse getFinancialReport() {
        List<Transaction> all = transactionRepository.findAll();

        List<TransactionResponse> details = all.stream()
                .map(TransactionResponse::from)
                .toList();

        int paidCount     = (int) all.stream().filter(Transaction::isPaid).count();
        double revenue    = transactionRepository.calculateTotalRevenue();
        double average    = paidCount > 0 ? revenue / paidCount : 0;

        return new FinancialReportResponse(
                LocalDateTime.now(),
                all.size(),
                paidCount,
                revenue,
                average,
                details
        );
    }
}