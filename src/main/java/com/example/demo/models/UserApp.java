package com.example.demo.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_app")
public class UserApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;


    public UserApp(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    public UserApp(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = Role.USER;
    }
}
