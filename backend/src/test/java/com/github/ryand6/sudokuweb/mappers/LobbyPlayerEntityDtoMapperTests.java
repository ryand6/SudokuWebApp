package com.github.ryand6.sudokuweb.mappers;

import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.LobbyPlayerId;
import com.github.ryand6.sudokuweb.dto.entity.LobbyPlayerDto;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyPlayerEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.ScoreEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.UserEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class LobbyPlayerEntityDtoMapperTests extends AbstractIntegrationTest {

    private UserRepository userRepository;
    private LobbyRepository lobbyRepository;
    private LobbyPlayerEntityDtoMapper lobbyPlayerEntityDtoMapper;
    private UserEntityDtoMapper userEntityDtoMapper;
    private ScoreEntityDtoMapper scoreEntityDtoMapper;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        lobbyRepository = mock(LobbyRepository.class);
        scoreEntityDtoMapper = new ScoreEntityDtoMapper();
        userEntityDtoMapper = new UserEntityDtoMapper(scoreEntityDtoMapper);
        lobbyPlayerEntityDtoMapper = new LobbyPlayerEntityDtoMapper(userRepository, lobbyRepository, userEntityDtoMapper);
    }

//    @Test
//    public void mapToDto_mapsFieldsCorrectly() {
//        LobbyPlayerId id = new LobbyPlayerId(1L, 2L);
//        LobbyPlayerEntity entity = LobbyPlayerEntity.builder()
//                .id(id)
//                .joinedAt(Instant.now())
//                .build();
//
//        LobbyPlayerDto dto = lobbyPlayerEntityDtoMapper.mapToDto(entity);
//
//        assertThat(dto).isNotNull();
//        assertThat(dto.getId()).isEqualTo(id);
//        assertThat(dto.getJoinedAt()).isEqualTo(entity.getJoinedAt());
//    }

//    @Test
//    public void mapFromDto_mapsFieldsCorrectly() {
//        LobbyPlayerId id = new LobbyPlayerId(1L, 2L);
//        Instant now = Instant.now();
//        LobbyPlayerDto dto = LobbyPlayerDto.builder()
//                .id(id)
//                .joinedAt(now)
//                .build();
//
//        LobbyEntity lobby = LobbyEntity.builder().id(1L).lobbyName("test").build();
//        UserEntity user = UserEntity.builder().id(2L).username("user").build();
//
//        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
//        when(lobbyRepository.findById(1L)).thenReturn(Optional.of(lobby));
//
//        LobbyPlayerEntity entity = lobbyPlayerEntityDtoMapper.mapFromDto(dto);
//
//        assertThat(entity).isNotNull();
//        assertThat(entity.getId()).isEqualTo(id);
//        assertThat(entity.getJoinedAt()).isEqualTo(now);
//        assertThat(entity.getUser()).isEqualTo(user);
//        assertThat(entity.getLobby()).isEqualTo(lobby);
//    }
//
//    @Test
//    public void mapFromDto_userNotFound_throwsException() {
//        LobbyPlayerId id = new LobbyPlayerId(1L, 999L);
//        LobbyPlayerDto dto = LobbyPlayerDto.builder()
//                .id(id)
//                .joinedAt(Instant.now())
//                .build();
//
//        when(userRepository.findById(999L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> lobbyPlayerEntityDtoMapper.mapFromDto(dto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("User id not found: 999");
//    }
//
//    @Test
//    public void mapFromDto_lobbyNotFound_throwsException() {
//        LobbyPlayerId id = new LobbyPlayerId(999L, 2L);
//        LobbyPlayerDto dto = LobbyPlayerDto.builder()
//                .id(id)
//                .joinedAt(Instant.now())
//                .build();
//
//        when(userRepository.findById(2L)).thenReturn(Optional.of(new UserEntity()));
//        when(lobbyRepository.findById(999L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> lobbyPlayerEntityDtoMapper.mapFromDto(dto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Lobby id not found: 999");
//    }
}
