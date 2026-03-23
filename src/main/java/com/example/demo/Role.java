package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long role_id;

    private String name;

    // Getters and Setters
    public Long getRole_id() { return role_id; }
    public void setRole_id(Long role_id) { this.role_id = role_id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}