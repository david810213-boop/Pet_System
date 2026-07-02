package com.petgrooming.pet_system.repository;

import com.petgrooming.pet_system.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    // 查詢某個錢包的所有異動紀錄，依時間倒序
    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);
}
