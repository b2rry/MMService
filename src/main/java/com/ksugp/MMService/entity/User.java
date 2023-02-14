package com.ksugp.MMService.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users_table")
public class User {
    public User(){}
    public User(String username, String password, String email, String info, Role role, Status status) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.info = info;
        this.role = role;
        this.status = status;
    }
    public User(String username, String email, String info, Role role, Status status) {
        this.username = username;
        this.email = email;
        this.info = info;
        this.role = role;
        this.status = status;
    }
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
