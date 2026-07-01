package com.petgrooming.pet_system.dto;

import com.petgrooming.pet_system.enums.UserRole;
import com.petgrooming.pet_system.model.User;
import lombok.Data;

// API 回傳格式，刻意不含 password
@Data
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private UserRole role;

    public static UserResponse from(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setName(user.getName());
        res.setRole(user.getRole());
        return res;
    }
}
