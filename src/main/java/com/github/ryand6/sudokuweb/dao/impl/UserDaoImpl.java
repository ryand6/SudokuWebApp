package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.dao.UserDao;
import com.github.ryand6.sudokuweb.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
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

    @Override
    public Optional<User> findOne(Long userId) {
        List<User> results = jdbcTemplate.query(
                "SELECT id, username, password_hash, created_at FROM users WHERE id = ? LIMIT 1",
                new UserRowMapper(),
                userId
        );
        return results.stream().findFirst();
    }

    @Override
    public List<User> find() {
        return jdbcTemplate.query(
                "SELECT id, username, password_hash, created_at FROM users",
                new UserRowMapper()
        );
    }

    @Override
    public void update(Long userId, User user) {
        jdbcTemplate.update(
                "UPDATE users SET id = ?, username = ?, password_hash = ? WHERE id = ?",
                user.getId(), user.getUsername(), user.getPasswordHash(), userId
        );
    }

    @Override
    public void delete(Long userId) {
        jdbcTemplate.update(
                "DELETE FROM users WHERE id = ?",
                userId
        );
    }

    public static class UserRowMapper implements RowMapper<User> {

        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = User.builder().
                    id(rs.getLong("id")).
                    username(rs.getString("username")).
                    passwordHash(rs.getString("password_hash")).build();
            Timestamp createdTimestamp = rs.getTimestamp("created_at");
            // Convert timestamp to local date time and set the createdAt field if not null
            if (createdTimestamp != null) {
                LocalDateTime createdAt = createdTimestamp.toLocalDateTime();
                user.setCreatedAt(createdAt);
            }
            return user;
        }

    }

}
