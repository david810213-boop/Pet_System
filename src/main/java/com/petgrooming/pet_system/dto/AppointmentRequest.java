package com.petgrooming.pet_system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class AppointmentRequest {

    //從下拉選單選擇，不再手動輸入名字
    @NotNull(message = "請選擇寵物")
    private Long petId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> selectedItems;
}