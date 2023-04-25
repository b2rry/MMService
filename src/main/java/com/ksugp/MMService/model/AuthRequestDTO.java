package com.ksugp.MMService.model;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String email;
    private String password;
}
