package com.petgrooming.pet_system.config;

import com.petgrooming.pet_system.enums.UserRole;
import com.petgrooming.pet_system.model.GroomingItem;
import com.petgrooming.pet_system.model.User;
import com.petgrooming.pet_system.repository.GroomingItemRepository;
import com.petgrooming.pet_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final GroomingItemRepository groomingItemRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1. 初始化預設帳號
        createIfNotExists("admin@pet.com", "admin123", "系統管理員", UserRole.ADMIN);
        createIfNotExists("staff@pet.com", "staff123", "美容師小洪",  UserRole.STAFF);
        createIfNotExists("user@pet.com",  "user123",  "測試會員",    UserRole.CUSTOMER);
        log.info("預設帳號初始化完成");

        if (groomingItemRepository.count() == 0) {
            
            saveItem("GS001", "指甲修剪磨圓", "包含基本的指甲長度修剪與邊緣圓滑打磨", 200.0);
            saveItem("GS002", "剃腳底屁股毛", "局部毛髮修剪，保持寵物居家清潔與防滑", 150.0);
            saveItem("GS003", "擠肛門腺", "溫和清潔寵物肛門腺，減少異味與不適感", 180.0);
            saveItem("GS004", "耳道清潔", "使用寵物專用潔耳液，溫和清除耳垢", 100.0);
            saveItem("GS005", "手工吹整毛髮", "專業美容師手工吹整，打造蓬鬆順滑毛質", 300.0);
            saveItem("GS006", "腳緣修剪", "精修足部線條，讓腳掌看起來圓潤乾淨", 200.0);
            saveItem("GS007", "臉部精緻修容", "根據寵物臉型進行細緻修剪，視覺造型升級", 350.0);
            saveItem("GS008", "舒壓按摩", "舒緩寵物肌肉緊張，降低美容過程的焦慮感", 400.0);
            saveItem("GS009", "天然低敏結構式洗浴", "使用頂級天然低敏洗劑，深層修復受損毛髮", 500.0);
            saveItem("GS010", "護膚潤澤毛髮", "加強皮膚保濕與毛髮毛鱗片滋養", 280.0);
            saveItem("GS011", "毛鱗修復液", "專門針對乾枯毛髮設計的密集修護安瓶精華", 320.0);
            saveItem("GS012", "牙齒清潔", "使用寵物酵素牙膏，基本口腔清潔與除垢", 200.0);

            log.info("✨ [系統通知] 12項完整經典美容服務項目已成功初始化入庫！");
        }
    }

    // 封裝一個方便新增的輔助小方法
    private void saveItem(String code, String name, String desc, double price) {
        GroomingItem item = new GroomingItem();
        item.setItemCode(code);
        item.setName(name);
        item.setDescription(desc);
        item.setPrice(price);
        item.setDeleted(false); // 預設全部直接上架
        groomingItemRepository.save(item);
    }

    private void createIfNotExists(String username, String password, String name, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            log.info("帳號已存在，跳過建立：{}", username);
            return;
        }
        User user = User.builder()
                .username(username)
                .password(password)
                .name(name)
                .role(role)
                .isActive(true)
                .build();
        userRepository.save(user);
        log.info("建立預設帳號：{} ({})", username, role);
    }
}