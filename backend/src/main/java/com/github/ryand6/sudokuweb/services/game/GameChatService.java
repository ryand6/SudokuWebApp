package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.chat.GameChatMessageEntity;
import com.github.ryand6.sudokuweb.domain.game.chat.GameChatMessageFactory;
import com.github.ryand6.sudokuweb.domain.game.chat.GameChatMessageRepository;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerRepository;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.dto.entity.game.GameChatMessageDto;
import com.github.ryand6.sudokuweb.enums.MessageType;
import com.github.ryand6.sudokuweb.events.types.game.GameChatMessageSentWsEvent;
import com.github.ryand6.sudokuweb.exceptions.game.player.GamePlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.chat.MessageProfanityException;
import com.github.ryand6.sudokuweb.mappers.Impl.game.GameChatMessageEntityDtoMapper;
import com.github.ryand6.sudokuweb.validation.ProfanityValidator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameChatService {

    private final GamePlayerRepository gamePlayerRepository;
    private final GameChatMessageRepository gameChatMessageRepository;
    private final GameChatMessageEntityDtoMapper gameChatMessageEntityDtoMapper;
    private final ProfanityValidator profanityValidator;
    private final ApplicationEventPublisher applicationEventPublisher;

    public GameChatService(GamePlayerRepository gamePlayerRepository,
                           GameChatMessageRepository gameChatMessageRepository,
                           GameChatMessageEntityDtoMapper gameChatMessageEntityDtoMapper,
                           ProfanityValidator profanityValidator,
                           ApplicationEventPublisher applicationEventPublisher) {
        this.gamePlayerRepository = gamePlayerRepository;
        this.gameChatMessageRepository = gameChatMessageRepository;
        this.gameChatMessageEntityDtoMapper = gameChatMessageEntityDtoMapper;
        this.profanityValidator = profanityValidator;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<GameChatMessageDto> getGameChatMessages(Long gameId, int page) {
        Pageable pageable = PageRequest.of(page, GameChatMessageEntity.PAGE_SIZE, Sort.by(Sort.Direction.DESC, "created_at"));
        return gameChatMessageRepository.findByGameEntity_IdOrderByCreatedAtDesc(gameId, pageable)
                .stream()
                .map(gameChatMessageEntityDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createMessage(Long gameId, Long userId, String message, MessageType messageType) {
        validateMessageContent(message);
        GamePlayerEntity gamePlayer = gamePlayerRepository.findByCompositeId(gameId, userId)
                .orElseThrow(() -> new GamePlayerNotFoundException("Game player with game ID " + gameId + " and user ID " + userId + " does not exist"));
        GameChatMessageEntity gameChatMessage = GameChatMessageFactory.createGameChatMessage(gamePlayer.getGameEntity(), gamePlayer.getUserEntity(), message, messageType);
        Instant lastMessageTime = gamePlayer.getLastGameMessageTimestamp();
        gameChatMessage.validateMessageTime(lastMessageTime);
        GamePlayerEntity updatedGamePlayer = updateGameMessageTime(gamePlayer);
        gamePlayerRepository.save(updatedGamePlayer);
        gameChatMessage = gameChatMessageRepository.save(gameChatMessage);
        gameChatMessageRepository.flush();
        GameChatMessageDto chatMessageDto = gameChatMessageEntityDtoMapper.mapToDto(gameChatMessage);

        applicationEventPublisher.publishEvent(
                new GameChatMessageSentWsEvent(chatMessageDto)
        );
    }

    private GamePlayerEntity updateGameMessageTime(GamePlayerEntity gamePlayer) {
        gamePlayer.updateLastGameMessageTime();
        return gamePlayer;
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
