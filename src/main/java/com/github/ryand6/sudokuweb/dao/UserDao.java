package com.github.ryand6.sudokuweb.dao;

import com.github.ryand6.sudokuweb.domain.User;

import java.util.Optional;

public interface UserDao {

    void create(User user);

    Optional<User> findOne(Long userId);

}
