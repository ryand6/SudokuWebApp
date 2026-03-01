package com.github.ryand6.sudokuweb.mappers;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.score.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyPlayerRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
Integration tests for LobbyEntityDtoMapper
*/
public class LobbyEntityDtoMapperIntegrationTests extends AbstractIntegrationTest {

    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final LobbyPlayerRepository lobbyPlayerRepository;

    @Autowired
    public LobbyEntityDtoMapperIntegrationTests(
            UserRepository userRepository,
            LobbyRepository lobbyRepository,
            LobbyEntityDtoMapper lobbyEntityDtoMapper,
            LobbyPlayerRepository lobbyPlayerRepository) {
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
    }

    private ScoreEntity savedScore;
    private UserEntity savedUser;
    private LobbyEntity savedLobby;

//    @BeforeEach
//    public void setUp() {
//        // Setup test score data - don't save as the User object will save it via cascade.ALL
//        savedScore = TestDataUtil.createTestScoreA();
//        // Setup test user data in the test DB
//        savedUser = userRepository.save(TestDataUtil.createTestUserA(savedScore));
//
//        savedLobby = lobbyRepository.save(TestDataUtil.createTestLobbyA(savedUser));
//        LobbyPlayerEntity lobbyPlayerEntity = TestDataUtil.createTestLobbyPlayer(savedLobby, savedUser);
//        savedLobby.setLobbyPlayers(Set.of(lobbyPlayerEntity));
//        lobbyPlayerRepository.save(lobbyPlayerEntity);
//    }

//    @Test
//    void mapToDto_shouldReturnValidLobbyDto() {
//        LobbyDto dto = lobbyEntityDtoMapper.mapToDto(savedLobby);
//
//        assertThat(dto).isNotNull();
//        assertThat(dto.getId()).isEqualTo(savedLobby.getId());
//        assertThat(dto.getLobbyName()).isEqualTo(savedLobby.getLobbyName());
//        assertThat(dto.getIsActive()).isEqualTo(savedLobby.getIsActive());
//        assertThat(dto.getIsPublic()).isEqualTo(savedLobby.getIsPublic());
//        assertThat(dto.getInGame()).isEqualTo(savedLobby.getInGame());
//        assertThat(dto.getDifficulty()).isEqualTo(savedLobby.getDifficulty());
//        assertThat(dto.getHostId()).isEqualTo(savedLobby.getHost().getId());
//
//        assertThat(dto.getLobbyPlayers()).hasSize(1);
//        LobbyPlayerDto lobbyPlayerDto = dto.getLobbyPlayers().iterator().next();
//        assertThat(lobbyPlayerDto.getId().getUserId()).isEqualTo(savedUser.getId());
//    }
//
//    @Test
//    void mapFromDto_shouldReturnValidLobbyEntity() {
//        // Create a LobbyDto from saved user
//        LobbyPlayerDto lobbyPlayerDto = LobbyPlayerDto.builder()
//                .id(new LobbyPlayerId(savedLobby.getId(), savedUser.getId()))
//                .joinedAt(Instant.now())
//                .build();
//
//        LobbyDto dto = LobbyDto.builder()
//                .id(5L)
//                .lobbyName("Test Lobby")
//                .isActive(true)
//                .isPublic(false)
//                .inGame(false)
//                .lobbyPlayers(Set.of(lobbyPlayerDto))
//                .hostId(lobbyPlayerDto.getId().getUserId())
//                .build();
//
//        LobbyEntity entity = lobbyEntityDtoMapper.mapFromDto(dto);
//
//        assertThat(entity).isNotNull();
//        assertThat(entity.getId()).isEqualTo(5L);
//        assertThat(entity.getLobbyName()).isEqualTo("Test Lobby");
//        assertThat(entity.getIsActive()).isTrue();
//        assertThat(entity.getIsPublic()).isFalse();
//        assertThat(entity.getInGame()).isFalse();
//        assertThat(entity.getLobbyPlayers()).hasSize(1);
//        assertThat(entity.getHost().getId()).isEqualTo(savedUser.getId());
//
//        LobbyPlayerEntity lobbyPlayer = entity.getLobbyPlayers().iterator().next();
//        assertThat(lobbyPlayer.getId().getUserId()).isEqualTo(savedUser.getId());
//    }
//
//    @Test
//    void mapFromDto_shouldThrowExceptionForMissingHostId() {
//        // Create a LobbyDto from saved user
//        LobbyPlayerDto lobbyPlayerDto = LobbyPlayerDto.builder()
//                .id(new LobbyPlayerId(savedLobby.getId(), savedUser.getId()))
//                .joinedAt(Instant.now())
//                .build();
//
//        LobbyDto dto = LobbyDto.builder()
//                .lobbyName("Invalid Lobby")
//                .isActive(false)
//                .isPublic(true)
//                .inGame(false)
//                .lobbyPlayers(Set.of(lobbyPlayerDto))
//                .hostId(999L)
//                .build();
//
//        assertThatThrownBy(() -> lobbyEntityDtoMapper.mapFromDto(dto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("User with host id not found: 999");
//    }
//
//    @Test
//    void mapFromDto_shouldThrowExceptionForLobbyPlayerId() {
//        // Invalid user ID
//        LobbyPlayerDto invalidLobbyPlayerDto = LobbyPlayerDto.builder()
//                .id(new LobbyPlayerId(999L, 999L))
//                .joinedAt(Instant.now())
//                .build();
//
//        LobbyDto dto = LobbyDto.builder()
//                .lobbyName("Invalid Lobby")
//                .isActive(false)
//                .isPublic(true)
//                .inGame(false)
//                .lobbyPlayers(Set.of(invalidLobbyPlayerDto))
//                .hostId(savedUser.getId())
//                .build();
//
//        assertThatThrownBy(() -> lobbyEntityDtoMapper.mapFromDto(dto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Lobby Player id not found: ");
//    }

}
