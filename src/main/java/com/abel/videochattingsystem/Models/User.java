package com.abel.videochattingsystem.Models;


import com.abel.videochattingsystem.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String email;
    private String password;
    private String status;
    private Boolean enabled;
    private Boolean locked;
    @Enumerated(EnumType.STRING)
    private Role role;

    // Constructor without id
    public User(String username, String email, Boolean enabled, Boolean locked, String password, String status, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled =enabled;
        this.locked =locked;
        this.status = status;
        this.role = role;
    }

    // Constructor with id
    public User(Integer id, String username, String email, Boolean enabled, Boolean locked,  String password, String status, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
