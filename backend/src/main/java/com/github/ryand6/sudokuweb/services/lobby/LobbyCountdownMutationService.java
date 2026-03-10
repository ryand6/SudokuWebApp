package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.countdown.CountdownEvaluationResult;
import com.github.ryand6.sudokuweb.domain.lobby.countdown.LobbyCountdownEntity;
import com.github.ryand6.sudokuweb.events.types.lobby.LobbyCountdownResetEvent;
import com.github.ryand6.sudokuweb.exceptions.lobby.countdown.LobbyCountdownLockedException;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;


// Handles mutations on Lobby Countdown safely to account for concurrent operations
@Service
public class LobbyCountdownMutationService {

    /**
     * Safely evaluates the countdown with retries on OptimisticLockException.
     * Assumes the countdown entity is already managed in the current transaction.
     */
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @Transactional
    public CountdownEvaluationResult safeEvaluateCountdown(LobbyCountdownEntity countdown) {
        return countdown.evaluateCountdownState();
    }

    @Recover
    public CountdownEvaluationResult recoverFromSafeEvaluateCountdown(ObjectOptimisticLockingFailureException ex, LobbyCountdownEntity countdown) {
        throw new LobbyCountdownLockedException("Failed to evaluate countdown after retries");
    }

    /**
     * Safely resets countdown with retries on OptimisticLockException.
     * Assumes the countdown entity is already managed in the current transaction.
     */
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @Transactional
    public void safeCountdownReset(LobbyCountdownEntity countdown) {
        countdown.resetCountdownIfActive();
    }

    @Recover
    public void recoverFromSafeCountdownReset(ObjectOptimisticLockingFailureException ex, LobbyCountdownEntity countdown) {
        throw new LobbyCountdownLockedException("Failed to reset countdown after retries");
    }

    @EventListener
    void handleLobbyCountdownResetEvent(LobbyCountdownResetEvent event) {
        safeCountdownReset(event.getLobbyCountdown());
    }

}
