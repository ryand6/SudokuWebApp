package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.dao.UserDao;
import com.github.ryand6.sudokuweb.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(User user) {
        jdbcTemplate.update("INSERT INTO users (id, username, password_hash) VALUES (?, ?, ?)",
                user.getId(), user.getUsername(), user.getPasswordHash()
        );
    }

}
