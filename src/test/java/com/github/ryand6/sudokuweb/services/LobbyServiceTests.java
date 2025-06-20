package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.LobbyDto;
import com.github.ryand6.sudokuweb.exceptions.InvalidLobbyPublicStatusParametersException;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.services.impl.LobbyService;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class LobbyServiceTests {

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private UserService userService;

    @Mock
    private LobbyEntityDtoMapper lobbyEntityDtoMapper;

    @InjectMocks
    private LobbyService lobbyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void generateUniqueCode_verifyLoopIterations_checkCodeLengthAndClassType() {
        // Simulate first 2 attempts fail (return true), third attempt succeeds (false)
        when(lobbyRepository.existsByJoinCode(Mockito.anyString()))
                .thenReturn(true, true, false);

        String code = lobbyService.generateUniqueCode();

        // Verify existsByJoinCode called exactly 3 times - loop ran 3 times
        Mockito.verify(lobbyRepository, Mockito.times(3)).existsByJoinCode(Mockito.anyString());

        assertNotNull(code);
        assertInstanceOf(String.class, code);
        assertEquals(25, code.length());
    }

    @Test
    public void createNewLobby_invalidIsPublicAndIsPrivateParameters() {
        String lobbyName = "Test Lobby";
        boolean isPublic = false;
        boolean isPrivate = false;
        String joinCode = null;
        Long requesterId = 1L;
        InvalidLobbyPublicStatusParametersException ex = assertThrows(
                InvalidLobbyPublicStatusParametersException.class,
                () -> lobbyService.createNewLobby(lobbyName, isPublic, isPrivate, joinCode, requesterId)
        );
        assertThat(ex.getMessage()).isEqualTo("Either Public or Private lobby must be checked");
    }

    @Test
    public void createNewLobby_successfulCreation() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("Test User");
        when(userService.findUserById(anyLong())).thenReturn(user);

        LobbyEntity lobby = new LobbyEntity();
        lobby.setId(1L);
        lobby.setLobbyName("Test Lobby");
        when(lobbyRepository.save(any(LobbyEntity.class))).thenReturn(lobby);

        LobbyDto lobbyDto = new LobbyDto();
        lobbyDto.setId(1L);
        lobbyDto.setLobbyName("Test Lobby");
        lobbyDto.setIsPublic(true);
        when(lobbyEntityDtoMapper.mapToDto(any(LobbyEntity.class))).thenReturn(lobbyDto);

        String lobbyName = "Test Lobby";
        boolean isPublic = true;
        boolean isPrivate = false;
        String joinCode = null;
        Long requesterId = 1L;
        LobbyDto lobbyDtoTest = lobbyService.createNewLobby(lobbyName, isPublic, isPrivate, joinCode, requesterId);
        assertThat(lobbyDtoTest.getId()).isEqualTo(1L);
        assertThat(lobbyDtoTest.getLobbyName()).isEqualTo("Test Lobby");
        assertThat(lobbyDtoTest.getIsPublic()).isEqualTo(true);
    }

    @Test
    public void getPublicLobbies_testExpectedReturnList() {
        int page = 0;
        int size = 2;

        LobbyEntity lobby1 = new LobbyEntity();
        lobby1.setId(1L);
        LobbyEntity lobby2 = new LobbyEntity();
        lobby2.setId(2L);

        Page<LobbyEntity> lobbyPage = new PageImpl<>(List.of(lobby1, lobby2));

        when(lobbyRepository.findByIsPublicTrueAndIsActiveTrue(PageRequest.of(page, size, Sort.by("createdAt").descending())))
                .thenReturn(lobbyPage);

        LobbyDto dto1 = new LobbyDto();
        dto1.setId(1L);
        LobbyDto dto2 = new LobbyDto();
        dto2.setId(2L);

        when(lobbyEntityDtoMapper.mapToDto(lobby1)).thenReturn(dto1);
        when(lobbyEntityDtoMapper.mapToDto(lobby2)).thenReturn(dto2);

        List<LobbyDto> result = lobbyService.getPublicLobbies(page, size);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
    }

}
