package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserDaoImplTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private UserDaoImpl underTest;

    @Test
    public void testCreateUserSql() {
        User user = TestDataUtil.createTestUserA();

        underTest.create(user);

        verify(jdbcTemplate).update(
                eq("INSERT INTO users (id, username, password_hash) VALUES (?, ?, ?)"),
                eq(1L), eq("Henry"), eq("a4ceE42GHa")
        );
    }

    @Test
    public void testFindOneUserSql() {
        underTest.findOne(1L);

        verify(jdbcTemplate).query(
                eq("SELECT id, username, password_hash, created_at FROM users WHERE id = ? LIMIT 1"),
                ArgumentMatchers.<UserDaoImpl.UserRowMapper>any(),
                eq(1L)
        );
    }

    @Test
    public void testFindManyUsersSql() {
        underTest.find();
        verify(jdbcTemplate).query(
                eq("SELECT id, username, password_hash, created_at FROM users"),
                ArgumentMatchers.<UserDaoImpl.UserRowMapper>any()
        );
    }

}
