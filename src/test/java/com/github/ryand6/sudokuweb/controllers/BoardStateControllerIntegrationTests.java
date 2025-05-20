package com.github.ryand6.sudokuweb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.GenerateBoardRequestDto;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class BoardStateControllerIntegrationTests {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;

    @Autowired
    public BoardStateControllerIntegrationTests(
            MockMvc mockMvc,
            UserRepository userRepository,
            LobbyRepository lobbyRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void generateSudokuBoard_returnsHttp200Response() throws Exception {

        // Lobby needs to exist for generateSudokuBoard() to work
        ScoreEntity score = TestDataUtil.createTestScoreA();
        UserEntity user = TestDataUtil.createTestUserA(score);
        userRepository.save(user);
        LobbyEntity lobby = lobbyRepository.save(TestDataUtil.createTestLobbyA(user));

        GenerateBoardRequestDto requestDto = GenerateBoardRequestDto.builder()
                .difficulty("easy")
                .lobbyId(lobby.getId())
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/generate-board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

}
