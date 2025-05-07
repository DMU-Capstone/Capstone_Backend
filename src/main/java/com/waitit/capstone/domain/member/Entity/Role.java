package com.waitit.capstone.domain.member.Entity;

import lombok.Getter;

@Getter
public enum Role {
    USER("USER"), ADMIN("ADMIN");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public static Role fromRoleName(String roleNameWithPrefix) {

        if (roleNameWithPrefix == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }

        // 입력된 문자열에서 "ROLE_" 접두사 제거 (대소문자 구분 없이)
        String roleName = roleNameWithPrefix.toUpperCase().startsWith("ROLE_")
                ? roleNameWithPrefix.substring(5) // "ROLE_" 다음부터 문자열 추출
                : roleNameWithPrefix; // 접두사 없으면 그대로 사용

        for (Role role : values()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 권한입니다: " + roleName);
    }
}