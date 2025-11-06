package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.dto.entity.LobbyPlayerDto;
import com.github.ryand6.sudokuweb.exceptions.LobbyPlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.MessageProfanityException;
import com.github.ryand6.sudokuweb.exceptions.MessageTooSoonException;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyPlayerEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyPlayerRepository;
import com.github.ryand6.sudokuweb.validation.ProfanityValidator;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.time.Instant;
import java.util.Optional;

@Service
public class LobbyChatService {

    private final LobbyPlayerRepository lobbyPlayerRepository;
    private final LobbyPlayerEntityDtoMapper lobbyPlayerEntityDtoMapper;
    private final ProfanityValidator profanityValidator;

    public LobbyChatService(LobbyPlayerRepository lobbyPlayerRepository,
                            LobbyPlayerEntityDtoMapper lobbyPlayerEntityDtoMapper,
                            ProfanityValidator profanityValidator) {
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.lobbyPlayerEntityDtoMapper = lobbyPlayerEntityDtoMapper;
        this.profanityValidator = profanityValidator;
    }

    public LobbyPlayerDto submitMessage(Long lobbyId, Long userId, String message) {
        validateMessageContent(message);
        Optional<LobbyPlayerEntity> lobbyPlayerRequesterOptional = lobbyPlayerRepository.findByCompositeId(lobbyId, userId);
        if (lobbyPlayerRequesterOptional.isEmpty()) {
            throw new LobbyPlayerNotFoundException("Lobby Player with Lobby ID " + lobbyId + " and User ID " + userId + " does not exist");
        }
        LobbyPlayerEntity lobbyPlayerRequester = lobbyPlayerRequesterOptional.get();
        Instant lastMessageTime = lobbyPlayerRequester.getLobbyMessageTimestamp();
        if (lastMessageTime != null) {
            validateMessageTime(lastMessageTime);
        }
        LobbyPlayerEntity updatedLobbyPlayerRequester = updateLobbyMessageTime(lobbyPlayerRequester);
        lobbyPlayerRepository.save(updatedLobbyPlayerRequester);
        return lobbyPlayerEntityDtoMapper.mapToDto(updatedLobbyPlayerRequester);
    }

    private LobbyPlayerEntity updateLobbyMessageTime(LobbyPlayerEntity lobbyPlayer) {
        Instant now = Instant.now();
        lobbyPlayer.setLobbyMessageTimestamp(now);
        return lobbyPlayer;
    }

    // Validate if user can send another message in the lobby chat yet (5 second cool down period)
    private void validateMessageTime(Instant lastMessageTime) {
        // User must wait 5 seconds before another message can be sent
        Long timeSinceMessage = Instant.now().getEpochSecond() - lastMessageTime.getEpochSecond();
        if (timeSinceMessage < 5) {
            Long remainingSeconds = 5 - timeSinceMessage;
            throw new MessageTooSoonException(
                    "Please wait " + remainingSeconds + " more seconds before sending another message",
                    remainingSeconds
            );
        }
    }

    // Check for profanity in message contents
    private void validateMessageContent(String message) {
        String[] messageWords = message.split(" ");
        for (String word: messageWords) {
            boolean isClean = profanityValidator.isValid(message);
            if (!isClean) throw new MessageProfanityException("Message contains prohibited content");
        }
    }

}
