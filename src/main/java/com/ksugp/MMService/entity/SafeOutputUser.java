package com.ksugp.MMService.entity;

import lombok.Data;

@Data
public class SafeOutputUser {
    private int num;
    private Long id;
    private String username;
    private String email;
    private String info;
    private Role role;
    private Status status;

    public SafeOutputUser(int num, Long id, String username, String email, String info, Role role, Status status) {
        this.num = num;
        this.id = id;
        this.username = username;
        this.email = email;
        this.info = info;
        this.role = role;
        this.status = status;
    }
}
