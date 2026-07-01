package com.petgrooming.pet_system.repository;

import com.petgrooming.pet_system.enums.UserRole;
import com.petgrooming.pet_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    // 依 LINE userId 查詢（顧客 LINE 登入用）
    Optional<User> findByLineUserId(String lineUserId);

    // 依角色查詢
    List<User> findByRole(UserRole role);
}