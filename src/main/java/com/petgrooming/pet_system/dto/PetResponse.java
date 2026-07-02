package com.petgrooming.pet_system.dto;

import com.petgrooming.pet_system.enums.PetType;
import com.petgrooming.pet_system.model.Pet;
import lombok.Data;

// 專門控制 API 回傳格式，不直接回傳 entity
@Data
public class PetResponse {
    private Long id;
    private String name;
    private PetType petType;        // String → PetType enum，與 PetRequest 對齊
    private String petTypeLabel;    // 中文顯示名稱，例如「狗」，方便前端直接顯示
    private String breed;
    private Double weight;
    private Integer age;
    private String ownerName;       // 只回傳名字，不回傳整個 User 物件

    // 從 Pet entity 轉換成 DTO 的靜態工廠方法
    public static PetResponse from(Pet pet) {
        PetResponse res = new PetResponse();
        res.setId(pet.getId());
        res.setName(pet.getName());
        res.setPetType(pet.getPetType());
        res.setPetTypeLabel(pet.getPetType().getDescription()); // 中文說明一起帶出
        res.setBreed(pet.getBreed());
        res.setWeight(pet.getWeight());
        res.setAge(pet.getAge());
        res.setOwnerName(pet.getOwner().getName());
        return res;
    }
}
