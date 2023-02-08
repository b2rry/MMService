package com.ksugp.MMService.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users_table")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_username")
    private String username;
    @Column(name = "user_password")
    private String password;
    @Column(name = "user_email")
    private String email;
    @Column(name = "user_info")
    private String info;
    @Column(name = "user_role")
    @Enumerated(value = EnumType.STRING)
    private Role role;
    @Column(name = "user_status")
    @Enumerated(value = EnumType.STRING)
    private Status status;

}
