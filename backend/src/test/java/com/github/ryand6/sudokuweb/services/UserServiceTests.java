package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.ScoreDto;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.exceptions.UsernameTakenException;
import com.github.ryand6.sudokuweb.mappers.Impl.UserEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
Not using @SpringBootTest because these are unit tests only testing service method functionality and mocking the rest.
Was finding that @SpringBootTest was injecting real beans that were getting used instead of the mocked beans, no need to
simulate proper app environment for tests
 */
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEntityDtoMapper userEntityDtoMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getCurrentUserByOAuth_testFoundUser() {
        OAuth2User principal = mock(OAuth2User.class);
        OAuth2AuthenticationToken authToken = mock(OAuth2AuthenticationToken.class);
        when(authToken.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(principal.getName()).thenReturn("google-123");

        ScoreEntity score = new ScoreEntity();
        score.setTotalScore(0);
        score.setGamesPlayed(0);
        // Persist the user entity to DB
        UserEntity user = new UserEntity();
        user.setUsername("username");
        user.setProvider("google");
        user.setProviderId("google-123");
        user.setIsOnline(true);
        user.setScoreEntity(score);

        when(userRepository.findByProviderAndProviderId(any(String.class), any(String.class))).thenReturn(Optional.of(user));

        UserDto mockDto = new UserDto();
        mockDto.setUsername("username");
        mockDto.setIsOnline(true);
        ScoreDto scoreDto = new ScoreDto();
        scoreDto.setTotalScore(0);
        scoreDto.setGamesPlayed(0);
        mockDto.setScore(scoreDto);

        when(userEntityDtoMapper.mapToDto(any(UserEntity.class))).thenReturn(mockDto);

        UserDto userDto = userService.getCurrentUserByOAuth(principal, authToken);
        assertThat(userDto.getUsername()).isEqualTo("username");
        assertThat(userDto.getIsOnline()).isEqualTo(true);
        assertThat(userDto.getScore().getTotalScore()).isEqualTo(0);
        assertThat(userDto.getScore().getGamesPlayed()).isEqualTo(0);
    }

    @Test
    public void getCurrentUserByOAuth_testNotFoundUser() {
        OAuth2User principal = mock(OAuth2User.class);
        OAuth2AuthenticationToken authToken = mock(OAuth2AuthenticationToken.class);
        when(authToken.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(principal.getName()).thenReturn("google-123");

        ScoreEntity score = new ScoreEntity();
        score.setTotalScore(0);
        score.setGamesPlayed(0);
        // Persist the user entity to DB
        UserEntity user = new UserEntity();
        user.setUsername("username");
        user.setProvider("google");
        user.setProviderId("google-123");
        user.setIsOnline(true);
        user.setScoreEntity(score);

        when(userRepository.findByProviderAndProviderId(any(String.class), any(String.class))).thenReturn(Optional.empty());

        UserDto mockDto = new UserDto();
        mockDto.setUsername("username");
        mockDto.setIsOnline(true);
        ScoreDto scoreDto = new ScoreDto();
        scoreDto.setTotalScore(0);
        scoreDto.setGamesPlayed(0);
        mockDto.setScore(scoreDto);

        when(userEntityDtoMapper.mapToDto(any(UserEntity.class))).thenReturn(mockDto);

        assertThat(userService.getCurrentUserByOAuth(principal, authToken)).isNull();
    }

    @Test
    public void createNewUser_testUserTaken() {
        String username = "Test";
        String provider = "google";
        String providerId = "google-123";

        when(userRepository.existsByUsername(any(String.class))).thenReturn(true);

        UsernameTakenException ex = assertThrows(
                UsernameTakenException.class,
                () -> userService.createNewUser(username, provider, providerId)
        );
        assertThat(ex.getMessage()).isEqualTo("Username provided is taken, please choose another");
    }

    @Test
    public void createNewUser_successfulCreation() {
        String username = "Test";
        String provider = "google";
        String providerId = "google-123";

        Optional<UserEntity> nonUser = userRepository.findByProviderAndProviderId(provider, providerId);
        assertThat(nonUser.isPresent()).isEqualTo(false);

        when(userRepository.existsByUsername(any(String.class))).thenReturn(false);

        userService.createNewUser(username, provider, providerId);

        Optional<UserEntity> newUser = userRepository.findByProviderAndProviderId(provider, providerId);
        assertThat(newUser.isPresent()).isEqualTo(true);
        UserEntity newUserEntity = newUser.get();
        assertThat(newUserEntity.getUsername()).isEqualTo("Test");
        assertThat(newUserEntity.getIsOnline()).isEqualTo(true);
        assertThat(newUserEntity.getScoreEntity().getTotalScore()).isEqualTo(0);
        assertThat(newUserEntity.getScoreEntity().getGamesPlayed()).isEqualTo(0);
    }

    @Test
    public void updateUsername_testUserTaken() {
        String username = "Test";
        OAuth2User principal = mock(OAuth2User.class);
        OAuth2AuthenticationToken authToken = mock(OAuth2AuthenticationToken.class);

        when(userRepository.existsByUsername(any(String.class))).thenReturn(true);

        UsernameTakenException ex = assertThrows(
                UsernameTakenException.class,
                () -> userService.updateUsername(username, principal, authToken)
        );
        assertThat(ex.getMessage()).isEqualTo("Username provided is taken, please choose another");
    }

    @Test
    public void testGetPlayerRank() {
        when(userRepository.getUserRankById(any(Long.class))).thenReturn(1L);
        assertThat(userService.getPlayerRank(5L)).isEqualTo(1L);
    }

    @Test
    void getTop5PlayersTotalScore_returnsList() {
        UserEntity user1 = new UserEntity();
        UserEntity user2 = new UserEntity();
        List<UserEntity> users = List.of(user1, user2);
        Page<UserEntity> page = new PageImpl<>(users);

        when(userRepository.findByOrderByScoreEntity_TotalScoreDesc(any(Pageable.class))).thenReturn(page);
        when(userEntityDtoMapper.mapToDto(any())).thenReturn(new UserDto());

        List<UserDto> result = userService.getTop5PlayersTotalScore();

        assertEquals(2, result.size());
        // Logic to confirm the number of times methods were called is as expected during the test
        verify(userRepository).findByOrderByScoreEntity_TotalScoreDesc(any(Pageable.class));
        verify(userEntityDtoMapper, times(2)).mapToDto(any());
    }

}
