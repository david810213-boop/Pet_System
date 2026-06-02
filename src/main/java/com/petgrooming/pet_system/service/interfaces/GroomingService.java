package com.petgrooming.pet_system.service.interfaces;


import java.util.List;

import com.petgrooming.pet_system.dto.GroomingItemRequest;
import com.petgrooming.pet_system.dto.GroomingItemResponse;

public interface GroomingService {
    // 1. 獲取所有未刪除的美容項目選單
    List<GroomingItemResponse> getAllItems();

    // 2. 修改指定的美容項目
    GroomingItemResponse updateItem(Long id, GroomingItemRequest request);
    
    //3. 新建项目
    void createItem(GroomingItemRequest request);

    // 4. 邏輯刪除指定的美容項目
    void deleteItem(Long id);
}