package com.tranquility.auth.service;

import com.tranquility.common.auth.UserAuthPort;
import com.tranquility.auth.model.AuthenticatedUser;
import com.tranquility.common.user.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService implements UserDetailsService {

    private final UserAuthPort userAuthPort;

    @Override
    public AuthenticatedUser loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPrincipal user = userAuthPort.findUserByEmail(username);

        return AuthenticatedUser.from(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }
}