package com.tranquility.auth.model;

import com.tranquility.common.model.Role;
import com.tranquility.common.user.model.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AuthenticatedUser implements UserDetails, UserPrincipal {
    private final UUID id;
    private final String email;
    private final String password;
    private final Role role;

    private AuthenticatedUser(UUID id, String email, String password, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static AuthenticatedUser from(UUID id, String email, String password, Role role) {
        return new AuthenticatedUser(id, email, password, role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(
                role.getAuthority()
        ));
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Role getRole() {
        return role;
    }

    @Override
    public String getUsername() {
        return email;
    }
}