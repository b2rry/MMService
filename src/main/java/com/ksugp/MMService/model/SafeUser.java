package com.ksugp.MMService.model;

import jakarta.validation.constraints.Email;
import lombok.Data;


@Data
public class SafeUser {
    private int num = -1;
    private Long id = -1L;
    private String username = "default";
    @Email
    private String email = "default@email";
    private String info = "-";
    private Role role = Role.USER;
    private Status status = Status.ACTIVE;
    private String token = "NOTOKEN";

    public SafeUser(int num, Long id, String username, String email, String info, Role role, Status status) {
        this.num = num;
        this.id = id;
        this.username = username;
        this.email = email;
        this.info = info;
        this.role = role;
        this.status = status;
    }
    public SafeUser(){}
}
