package com.github.ryand6.sudokuweb.dao;

import com.github.ryand6.sudokuweb.dao.impl.UserDaoImpl;
import com.github.ryand6.sudokuweb.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        User user = User.builder().
                id(1L).
                username("Henry").
                passwordHash("a4ceE42GHa").
                build();

        underTest.create(user);

        verify(jdbcTemplate).update(
                eq("INSERT INTO users (id, username, password_hash) VALUES (?, ?, ?)"),
                eq(1L), eq("Henry"), eq("a4ceE42GHa")
        );
    }

}
