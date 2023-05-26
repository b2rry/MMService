package com.ksugp.MMService.model;

import lombok.Data;

@Data
public class UserResponse{
    private String mid;

    private String username;

    private String email;

    private String info;

    private String role;

    private Status status;

    private String latitude;

    private String longitude;

    private String timestamp;

    private  String color;
}
