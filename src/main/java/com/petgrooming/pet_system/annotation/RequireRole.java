package com.petgrooming.pet_system.annotation;

import com.petgrooming.pet_system.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色權限注解
 * 可標記在方法或類別上，指定允許存取的角色
 *
 * 使用方式：
 *   @RequireRole(UserRole.ADMIN)                       → 只有 ADMIN
 *   @RequireRole({UserRole.ADMIN, UserRole.STAFF})     → ADMIN 或 STAFF
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    UserRole[] value();
}
