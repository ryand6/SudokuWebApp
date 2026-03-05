package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.chat.LobbyChatMessageEntity;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.enums.MessageType;
import com.github.ryand6.sudokuweb.exceptions.lobby.player.LobbyPlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.chat.MessageProfanityException;
import com.github.ryand6.sudokuweb.mappers.Impl.lobby.LobbyChatMessageEntityDtoMapper;
import com.github.ryand6.sudokuweb.domain.lobby.chat.LobbyChatMessageRepository;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerRepository;
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

    private final LobbyPlayerRepository lobbyPlayerRepository;
    private final LobbyChatMessageRepository lobbyChatMessageRepository;
    private final LobbyChatMessageEntityDtoMapper lobbyChatMessageEntityDtoMapper;
    private final ProfanityValidator profanityValidator;

    public LobbyChatService(LobbyPlayerRepository lobbyPlayerRepository,
                            LobbyChatMessageRepository lobbyChatMessageRepository,
                            LobbyChatMessageEntityDtoMapper lobbyChatMessageEntityDtoMapper,
                            ProfanityValidator profanityValidator) {
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
        LobbyChatMessageEntity lobbyChatMessage = new LobbyChatMessageEntity();
        Instant lastMessageTime = lobbyPlayerRequester.getLastLobbyMessageTimestamp();
        lobbyChatMessage.validateMessageTime(lastMessageTime);
        LobbyPlayerEntity updatedLobbyPlayerRequester = updateLobbyMessageTime(lobbyPlayerRequester);
        lobbyPlayerRepository.save(updatedLobbyPlayerRequester);
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
        lobbyPlayer.setLastLobbyMessageTimestamp(now);
        return lobbyPlayer;
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
