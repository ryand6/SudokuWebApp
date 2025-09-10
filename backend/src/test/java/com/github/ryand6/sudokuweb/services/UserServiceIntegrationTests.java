package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Testcontainers
class UserServiceIntegrationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    // Dynamically register Postgres container properties with Spring
    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private final UserService userService;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    // Allows us to partially mock so that methods within the service can be mocked
    private UserService spyUserService;

    @Autowired
    UserServiceIntegrationTests(UserService userService,
                                UserRepository userRepository,
                                JdbcTemplate jdbcTemplate) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setup() {
        jdbcTemplate.execute(
                "TRUNCATE TABLE game_state, games, lobby_players, lobbies, users, scores, sudoku_puzzles RESTART IDENTITY CASCADE"
        );
        this.spyUserService = Mockito.spy(userService);
    }

    @Test
    void createNewUser_successfulCreation() {
        String username = "TestUser";
        String provider = "google";
        String providerId = "google-123";

        // Verify user does not exist initially
        Optional<UserEntity> before = userRepository.findByProviderAndProviderId(provider, providerId);
        assertThat(before).isEmpty();

        // Create user
        userService.createNewUser(username, provider, providerId);

        // Verify user now exists
        Optional<UserEntity> after = userRepository.findByProviderAndProviderId(provider, providerId);
        assertThat(after).isPresent();

        UserEntity created = after.get();
        assertThat(created.getUsername()).isEqualTo(username);
        assertThat(created.getIsOnline()).isTrue();
        assertThat(created.getScoreEntity().getTotalScore()).isEqualTo(0);
        assertThat(created.getScoreEntity().getGamesPlayed()).isEqualTo(0);
    }

    @Test
    void updateUsername_successfulUpdate() {
        String originalUsername = "OriginalUser";
        String provider = "google";
        String providerId = "google-123";

        // Create initial user
        userService.createNewUser(originalUsername, provider, providerId);

        Optional<UserEntity> userOpt = userRepository.findByProviderAndProviderId(provider, providerId);
        assertThat(userOpt).isPresent();
        UserEntity user = userOpt.get();

        // Mock OAuth2 principal (just to satisfy method signature)
        OAuth2User principal = mock(OAuth2User.class);
        OAuth2AuthenticationToken authToken = mock(OAuth2AuthenticationToken.class);
        // Mock method that requires OAuth principal and token
        doReturn(user).when(spyUserService).getCurrentUserEntityByOAuth(any(), any());

        // Update username
        String newUsername = "UpdatedUser";
        spyUserService.updateUsername(newUsername, principal, authToken);

        // Verify updated
        Optional<UserEntity> updatedOpt = userRepository.findByProviderAndProviderId(provider, providerId);
        assertThat(updatedOpt).isPresent();
        UserEntity updated = updatedOpt.get();
        assertThat(updated.getUsername()).isEqualTo(newUsername);
    }

}
