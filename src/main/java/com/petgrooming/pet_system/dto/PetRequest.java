package com.petgrooming.pet_system.dto;

import com.petgrooming.pet_system.enums.PetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PetRequest {

    @NotBlank(message = "寵物名稱不能為空")
    private String name;

    // 改為 PetType enum，前端只能傳 DOG / CAT / OTHER
    @NotNull(message = "寵物類型不能為空")
    private PetType petType;

    @NotBlank(message = "品種不能為空")
    private String breed;

    @NotNull(message = "體重不能為空")
    @Positive(message = "體重必須大於 0")
    private Double weight;

    @NotNull(message = "年齡不能為空")
    @PositiveOrZero(message = "年齡不能為負數")
    private Integer age;
}
