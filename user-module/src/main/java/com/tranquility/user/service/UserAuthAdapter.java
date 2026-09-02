package com.tranquility.user.service;

import com.tranquility.common.auth.UserAuthPort;
import com.tranquility.common.user.exception.UserAlreadyExistsException;
import com.tranquility.common.user.exception.UserNotFoundException;
import com.tranquility.common.model.Role;
import com.tranquility.user.repository.UserRepository;
import com.tranquility.user.entity.User;
import com.tranquility.common.user.model.UserPrincipal;
import com.tranquility.user.entity.UserProfile;
import com.tranquility.user.entity.UserStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAuthAdapter implements UserAuthPort {

    private final UserRepository repository;

    @Override
    public UserPrincipal findUserByEmail(String email) {
        return repository.findByEmail(email, UserPrincipal.class).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void createUserForExternalAuth(String name, String email, String password, String picture) {
        if (repository.findByEmail(email, UserPrincipal.class).isPresent()) throw new UserAlreadyExistsException();

        User user = User.create(
                email,
                password,
                Role.USER,
                UserProfile.create(name, email, picture),
                UserStats.create()
        );

        repository.save(user);
    }

    @Transactional
    public void createUserForEmailAuth(String name, String email, String password) {
        if (repository.findByEmail(email, UserPrincipal.class).isPresent()) throw new UserAlreadyExistsException();

        User user = User.create(
                email,
                password,
                Role.USER,
                UserProfile.create(name, email, null),
                UserStats.create()
        );

        repository.save(user);
    }
}
