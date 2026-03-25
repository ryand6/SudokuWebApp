package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.event.*;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.events.types.game.CreateBatchGameLogEvent;
import com.github.ryand6.sudokuweb.exceptions.game.GameEventSequenceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
public class GameEventService {

    private final GameEventSequenceRepository gameEventSequenceRepository;
    private final GameEventRepository gameEventRepository;
    private final EntityManager entityManager;

    public GameEventService(GameEventSequenceRepository gameEventSequenceRepository,
                            GameEventRepository gameEventRepository,
                            EntityManager entityManager) {
        this.gameEventSequenceRepository = gameEventSequenceRepository;
        this.gameEventRepository = gameEventRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void createGameEvents(Long gameId, Long userId, List<GameEventRequest> gameEventRequests) {
        GameEntity gameRef = entityManager.getReference(GameEntity.class, gameId);
        UserEntity userRef = entityManager.getReference(UserEntity.class, userId);

        List<GameEventEntity> gameEventEntities = gameEventRequests.stream()
                .map((req) ->
                        GameEventEntity.builder()
                                .gameEntity(gameRef)
                                .userEntity(userRef)
                                .eventType(req.getGameEventType())
                                .message(req.getMessage())
                                .sequenceNumber(getSequenceNumber(gameId))
                                .build())
                .toList();

        gameEventRepository.saveAll(gameEventEntities);

        // IMPLEMENT CALL TO WS
    }

    private long getSequenceNumber(Long gameId) {
        GameEventSequenceEntity gameEventSequence = gameEventSequenceRepository.findByGameIdForUpdate(gameId)
                .orElseThrow(() -> new GameEventSequenceNotFoundException("Game event sequence for game ID " + gameId + " does not exist."));
        return gameEventSequence.getCurrentSequenceNumberAndIncrement();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCreateGameEvents(CreateBatchGameLogEvent event) {
        createGameEvents(event.getGameId(), event.getUserId(), event.getGameEventRequests());
    }

}
