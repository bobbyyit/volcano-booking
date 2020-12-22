package com.volcano.repository;

import com.volcano.domain.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> getUser(String email);

    User createUser(User user);
}
