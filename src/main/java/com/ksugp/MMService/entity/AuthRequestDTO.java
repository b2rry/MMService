package com.ksugp.MMService.entity;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String email;
    private String password;
}
