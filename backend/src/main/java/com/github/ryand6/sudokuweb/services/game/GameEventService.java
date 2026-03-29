package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.event.*;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.events.types.game.CreateGameLogEvent;
import com.github.ryand6.sudokuweb.events.types.game.GameLogSendEvent;
import com.github.ryand6.sudokuweb.exceptions.game.GameEventSequenceNotFoundException;
import com.github.ryand6.sudokuweb.mappers.Impl.game.GameEventEntityDtoMapper;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class GameEventService {

    private final GameEventSequenceRepository gameEventSequenceRepository;
    private final GameEventRepository gameEventRepository;
    private final EntityManager entityManager;
    private final GameEventEntityDtoMapper gameEventEntityDtoMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public GameEventService(GameEventSequenceRepository gameEventSequenceRepository,
                            GameEventRepository gameEventRepository,
                            EntityManager entityManager,
                            GameEventEntityDtoMapper gameEventEntityDtoMapper,
                            ApplicationEventPublisher applicationEventPublisher) {
        this.gameEventSequenceRepository = gameEventSequenceRepository;
        this.gameEventRepository = gameEventRepository;
        this.entityManager = entityManager;
        this.gameEventEntityDtoMapper = gameEventEntityDtoMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public void createGameEvent(Long gameId, Long userId, GameEventRequest gameEventRequest) {
        GameEntity gameRef = entityManager.getReference(GameEntity.class, gameId);
        UserEntity userRef = entityManager.getReference(UserEntity.class, userId);

        GameEventEntity gameEvent = GameEventEntity.builder()
                                .gameEntity(gameRef)
                                .userEntity(userRef)
                                .eventType(gameEventRequest.getGameEventType())
                                .message(gameEventRequest.getMessage())
                                .sequenceNumber(getSequenceNumber(gameId))
                                .build();

        gameEventRepository.save(gameEvent);

        // call WS to broadcast event
        applicationEventPublisher.publishEvent(
                new GameLogSendEvent(gameEventEntityDtoMapper.mapToDto(gameEvent))
        );
    }

    private long getSequenceNumber(Long gameId) {
        GameEventSequenceEntity gameEventSequence = gameEventSequenceRepository.findByGameIdForUpdate(gameId)
                .orElseThrow(() -> new GameEventSequenceNotFoundException("Game event sequence for game ID " + gameId + " does not exist."));
        return gameEventSequence.getCurrentSequenceNumberAndIncrement();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handleCreateGameEvent(CreateGameLogEvent event) {
        createGameEvent(event.getGameId(), event.getUserId(), event.getGameEventRequest());
    }

}
