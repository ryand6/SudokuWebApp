package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.LobbyStateEntity;
import com.github.ryand6.sudokuweb.domain.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.LobbyStateDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.SudokuPuzzleRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class LobbyStateEntityDtoMapper implements EntityDtoMapper<LobbyStateEntity, LobbyStateDto> {

    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;

    public LobbyStateEntityDtoMapper(LobbyRepository lobbyRepository, UserRepository userRepository, SudokuPuzzleRepository sudokuPuzzleRepository) {
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
    }

    @Override
    public LobbyStateDto mapToDto(LobbyStateEntity lobbyStateEntity) {
        UserEntity user = lobbyStateEntity.getUserEntity();
        return LobbyStateDto.builder()
                .id(lobbyStateEntity.getId())
                .lobbyId(lobbyStateEntity.getLobbyEntity().getId())
                .userId(user.getId())
                .username(user.getUsername())
                .puzzleId(lobbyStateEntity.getSudokuPuzzleEntity().getId())
                .score(lobbyStateEntity.getScore())
                .currentBoardState(lobbyStateEntity.getCurrentBoardState())
                .lastActive(lobbyStateEntity.getLastActive())
                .build();
    }

    @Override
    public LobbyStateEntity mapFromDto(LobbyStateDto lobbyStateDto) {
        LobbyEntity lobbyEntity = resolveDtoLobby(lobbyStateDto.getLobbyId());
        UserEntity userEntity = resolveDtoUser(lobbyStateDto.getUserId());
        SudokuPuzzleEntity sudokuPuzzleEntity = resolveDtoPuzzle(lobbyStateDto.getPuzzleId());

        LobbyStateEntity.LobbyStateEntityBuilder lobbyStateEntityBuilder = LobbyStateEntity.builder()
                .lobbyEntity(lobbyEntity)
                .userEntity(userEntity)
                .sudokuPuzzleEntity(sudokuPuzzleEntity)
                .currentBoardState(lobbyStateDto.getCurrentBoardState())
                .score(lobbyStateDto.getScore());

        // Don't assign id field if non-existent, DB will create
        if (lobbyStateDto.getId() != null) {
            lobbyStateEntityBuilder.id(lobbyStateDto.getId());
        }

        return lobbyStateEntityBuilder.build();
    }

    // Get LobbyEntity through DTO LobbyId
    private LobbyEntity resolveDtoLobby(Long lobbyId) {
        return lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new EntityNotFoundException("Lobby not found with id " + lobbyId));
    }

    // Get UserEntity through DTO UserId
    private UserEntity resolveDtoUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));
    }

    // Get SudokuPuzzleEntity through DTO PuzzleId
    private SudokuPuzzleEntity resolveDtoPuzzle(Long puzzleId) {
        return sudokuPuzzleRepository.findById(puzzleId)
                .orElseThrow(() -> new EntityNotFoundException("Puzzle not found with id " + puzzleId));
    }
}
