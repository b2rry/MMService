package com.ksugp.MMService.model;

import lombok.Data;

import java.util.List;

@Data
public class UsersResponse {
    List<UserResponse> allUsers;
}
