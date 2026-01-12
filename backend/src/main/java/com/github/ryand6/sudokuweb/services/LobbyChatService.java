package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.domain.LobbyChatMessageEntity;
import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.enums.MessageType;
import com.github.ryand6.sudokuweb.exceptions.LobbyNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.LobbyPlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.MessageProfanityException;
import com.github.ryand6.sudokuweb.exceptions.MessageTooSoonException;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyChatMessageEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyChatMessageRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyPlayerRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.validation.ProfanityValidator;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LobbyChatService {

    private final UserService userService;
    private final LobbyRepository lobbyRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;
    private final LobbyChatMessageRepository lobbyChatMessageRepository;
    private final LobbyChatMessageEntityDtoMapper lobbyChatMessageEntityDtoMapper;
    private final ProfanityValidator profanityValidator;

    public LobbyChatService(UserService userService,
                            LobbyRepository lobbyRepository,
                            LobbyPlayerRepository lobbyPlayerRepository,
                            LobbyChatMessageRepository lobbyChatMessageRepository,
                            LobbyChatMessageEntityDtoMapper lobbyChatMessageEntityDtoMapper,
                            ProfanityValidator profanityValidator) {
        this.userService = userService;
        this.lobbyRepository = lobbyRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.lobbyChatMessageRepository = lobbyChatMessageRepository;
        this.lobbyChatMessageEntityDtoMapper = lobbyChatMessageEntityDtoMapper;
        this.profanityValidator = profanityValidator;
    }

    private final int PAGE_SIZE = 100;

    public List<LobbyChatMessageDto> getLobbyChatMessages(Long lobbyId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        // Return a list of 20 chat messages ordered newest to oldest
        return lobbyChatMessageRepository.findByLobbyEntity_IdOrderByCreatedAtDesc(lobbyId, pageable)
                .stream()
                .map(lobbyChatMessageEntityDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    // Method for submitting chat messages created by the user using the chat interface
    public LobbyChatMessageDto submitMessage(Long lobbyId, Long userId, String message) {
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
        LobbyChatMessageEntity lobbyChatMessage = new LobbyChatMessageEntity();
        lobbyChatMessage.setLobbyEntity(updatedLobbyPlayerRequester.getLobby());
        lobbyChatMessage.setUserEntity(updatedLobbyPlayerRequester.getUser());
        lobbyChatMessage.setMessage(message);
        lobbyChatMessage.setMessageType(MessageType.MESSAGE);
        lobbyChatMessage = lobbyChatMessageRepository.save(lobbyChatMessage);
        // Force Hibernate to flush, ensuring the row is inserted in the DB before returning (ensures createdAt is not null)
        lobbyChatMessageRepository.flush();
        return lobbyChatMessageEntityDtoMapper.mapToDto(lobbyChatMessage);
    }

    @Transactional
    // Method for submitting info messages triggered when a user carries out certain actions e.g. joins lobby, starts countdown, changes settings
    public LobbyChatMessageDto submitInfoMessage(Long lobbyId, Long userId, String message) {
        Optional<LobbyPlayerEntity> lobbyPlayerRequesterOptional = lobbyPlayerRepository.findByCompositeId(lobbyId, userId);
        if (lobbyPlayerRequesterOptional.isEmpty()) {
            throw new LobbyPlayerNotFoundException("Lobby Player with Lobby ID " + lobbyId + " and User ID " + userId + " does not exist");
        }
        LobbyPlayerEntity lobbyPlayerRequester = lobbyPlayerRequesterOptional.get();
        LobbyChatMessageEntity lobbyChatMessage = new LobbyChatMessageEntity();
        lobbyChatMessage.setLobbyEntity(lobbyPlayerRequester.getLobby());
        lobbyChatMessage.setUserEntity(lobbyPlayerRequester.getUser());
        lobbyChatMessage.setMessage(message);
        lobbyChatMessage.setMessageType(MessageType.INFO);
        lobbyChatMessage = lobbyChatMessageRepository.save(lobbyChatMessage);
        // Force Hibernate to flush, ensuring the row is inserted in the DB before returning (ensures createdAt is not null)
        lobbyChatMessageRepository.flush();
        return lobbyChatMessageEntityDtoMapper.mapToDto(lobbyChatMessage);
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
            boolean isClean = profanityValidator.isValid(word);
            if (!isClean) throw new MessageProfanityException("Message contains prohibited content");
        }
    }

}
