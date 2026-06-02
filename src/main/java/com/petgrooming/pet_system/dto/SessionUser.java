package com.petgrooming.pet_system.dto;

import com.petgrooming.pet_system.enums.UserRole;
import com.petgrooming.pet_system.model.User;
import lombok.Data;

import java.io.Serializable;

// 存入 HttpSession 的輕量物件
// 不存整個 User entity（避免 Lazy 關聯炸掉、entity 脫離 Session 報錯）
// 實作 Serializable，Session 序列化時才不會出錯
@Data
public class SessionUser implements Serializable {

    private Long id;
    private String username;
    private String name;
    private UserRole role;

    public static SessionUser from(User user) {
        SessionUser s = new SessionUser();
        s.setId(user.getId());
        s.setUsername(user.getUsername());
        s.setName(user.getName());
        s.setRole(user.getRole());
        return s;
    }

    // 角色判斷的便利方法，讓 Controller / Thymeleaf 都能使用
    public boolean isAdmin()    { return role == UserRole.ADMIN; }
    public boolean isStaff()    { return role == UserRole.STAFF; }
    public boolean isCustomer() { return role == UserRole.CUSTOMER; }
    public boolean isStaffOrAdmin() { return isStaff() || isAdmin(); }
}
