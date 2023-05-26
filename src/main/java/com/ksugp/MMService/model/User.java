package com.ksugp.MMService.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
//@Table(name = "users_table")
@Table(name = "users_table",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_email")
        }
)

public class User {
    public User() {
    }

    public User(String username, String password, String email, String info, Role role, Status status) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.info = info;
        this.role = role;
        this.status = status;
    }

    @Transient
    private int mid;
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_username")
    private String username;
    @Column(name = "user_password")
    private String password;
    @Transient
    private String passwordConfirm;
    @Transient
    private String confirmCheckPassword;
    @Column(name = "user_email", unique = true)
    private String email;
    @Column(name = "user_info")
    private String info;
    @Column(name = "user_role")
    @Enumerated(value = EnumType.STRING)
    private Role role;
    @Column(name = "user_status")
    @Enumerated(value = EnumType.STRING)
    private Status status;
    @Column(name = "user_latitude")
    private double latitude;
    @Column(name = "user_longitude")
    private double longitude;
    @Column(name = "user_timestamp")
    private Long timestamp;
    @Column(name = "user_color")
    private String color;
}
