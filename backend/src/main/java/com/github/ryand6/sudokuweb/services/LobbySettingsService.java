package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.dto.entity.LobbyPlayerDto;
import com.github.ryand6.sudokuweb.enums.PreferenceDirection;
import com.github.ryand6.sudokuweb.exceptions.LobbyPlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.VoteTooSoonException;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyPlayerEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyPlayerRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class LobbySettingsService {

    private final LobbyPlayerRepository lobbyPlayerRepository;
    private final LobbyPlayerEntityDtoMapper lobbyPlayerEntityDtoMapper;

    public LobbySettingsService(LobbyPlayerRepository lobbyPlayerRepository,
                                LobbyPlayerEntityDtoMapper lobbyPlayerEntityDtoMapper) {
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.lobbyPlayerEntityDtoMapper = lobbyPlayerEntityDtoMapper;
    }

    public LobbyPlayerDto submitDifficultyVote(Long lobbyId, Long userId, String voteType, PreferenceDirection preferenceDirection) {
        Optional<LobbyPlayerEntity> lobbyPlayerRequesterOptional = lobbyPlayerRepository.findByCompositeId(lobbyId, userId);
        if (lobbyPlayerRequesterOptional.isEmpty()) {
            throw new LobbyPlayerNotFoundException("Lobby Player with Lobby ID " + lobbyId + " and User ID " + userId + " does not exist");
        }
        LobbyPlayerEntity lobbyPlayerRequester = lobbyPlayerRequesterOptional.get();
        Instant lastVoteTime = getLastVoteTime(lobbyPlayerRequester, voteType);
        if (lastVoteTime != null) {
            // User must wait 10 seconds before another vote can be cast
            Long timePassedSinceVote = Instant.now().getEpochSecond() - lastVoteTime.getEpochSecond();
            if (timePassedSinceVote < 10) {
                Long remainingSeconds = 10 - timePassedSinceVote;
                throw new VoteTooSoonException(
                        "Please wait " + remainingSeconds + " more seconds before voting again",
                        remainingSeconds
                );
            }
        }
        lobbyPlayerRequester = updatePlayerVote(lobbyPlayerRequester, voteType, preferenceDirection);
        return lobbyPlayerEntityDtoMapper.mapToDto(lobbyPlayerRequester);
    }

    // Depending on voteType, retrieve the timestamp of the last vote cast for that voteType
    private Instant getLastVoteTime(LobbyPlayerEntity lobbyPlayer, String voteType) {
        switch (voteType.toLowerCase()) {
            case "difficulty":
                return lobbyPlayer.getDifficultyVoteTimestamp();
            case "duration":
                return lobbyPlayer.getDurationVoteTimestamp();
            default:
                throw new IllegalArgumentException("Invalid vote type: " + voteType);
        }
    }

    // Depending on voteType, update the relevant lobbyPlayer voting fields for the vote just cast
    private LobbyPlayerEntity updatePlayerVote(LobbyPlayerEntity lobbyPlayer, String voteType, PreferenceDirection preferenceDirection) {
        Instant now = Instant.now();

        switch(voteType.toLowerCase()) {
            case "difficulty", "duration":
                lobbyPlayer.setDifficultyPreference(preferenceDirection);
                lobbyPlayer.setDifficultyVoteTimestamp(now);
                return lobbyPlayer;
            default:
                throw new IllegalArgumentException("Invalid vote type: " + voteType);
        }

    }


}
