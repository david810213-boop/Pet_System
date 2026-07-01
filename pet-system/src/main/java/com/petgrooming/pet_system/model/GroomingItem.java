package com.petgrooming.pet_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grooming_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroomingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 保留原本 Enum 的編號（如 GS001, GS002），方便與舊代碼相容
    @Column(unique = true, nullable = false)
    private String itemCode;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double price;

    // 用一個布林值做「邏輯刪除」，避免直接刪除會導致以前的預約歷史紀錄找不到項目
    private boolean isDeleted = false;
}