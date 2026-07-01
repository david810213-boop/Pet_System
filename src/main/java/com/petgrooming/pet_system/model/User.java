package com.petgrooming.pet_system.model;

import com.petgrooming.pet_system.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;            // 登入帳號（email）

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;              // ADMIN / STAFF / CUSTOMER

    @Column(length = 100)
    private String email;               // 電子郵件（選填）

    @Column(length = 20)
    private String phone;               // 電話號碼（選填）

    @Column(name = "line_user_id", unique = true, length = 64)
    private String lineUserId;          // LINE 登入對應的 userId（顧客專用，選填）

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;    // 帳號是否啟用（預留停用功能）

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ── 寵物關聯 ─────────────────────────────────────────
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<Pet> pets = new ArrayList<>();

    // ── 便利方法 ──────────────────
    public boolean isAdmin()         { return role == UserRole.ADMIN; }
    public boolean isStaff()         { return role == UserRole.STAFF; }
    public boolean isCustomer()      { return role == UserRole.CUSTOMER; }
    public boolean isStaffOrAdmin()  { return isStaff() || isAdmin(); }

}
