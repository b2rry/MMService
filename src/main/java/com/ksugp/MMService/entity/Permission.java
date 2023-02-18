package com.ksugp.MMService.entity;

public enum Permission {
    USERS_READ("users:read"),
    USERS_WRITE("users:write"),
    USERS_WRITEPLUS("users:writeplus");
    private final String permission;
    Permission(String permission) {
        this.permission = permission;
    }
    public String getPermission() {
        return permission;
    }

}

