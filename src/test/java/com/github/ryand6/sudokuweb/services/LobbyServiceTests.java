package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.services.impl.LobbyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyServiceTests {

    @Mock
    private LobbyRepository lobbyRepository;

    @InjectMocks
    private LobbyService lobbyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void generateUniqueCode_verifyLoopIterations_checkCodeLengthAndClassType() {
        // Simulate first 2 attempts fail (return true), third attempt succeeds (false)
        Mockito.when(lobbyRepository.existsByJoinCode(Mockito.anyString()))
                .thenReturn(true, true, false);

        String code = lobbyService.generateUniqueCode();

        // Verify existsByJoinCode called exactly 3 times - loop ran 3 times
        Mockito.verify(lobbyRepository, Mockito.times(3)).existsByJoinCode(Mockito.anyString());

        assertNotNull(code);
        assertInstanceOf(String.class, code);
        assertEquals(25, code.length());
    }


}
