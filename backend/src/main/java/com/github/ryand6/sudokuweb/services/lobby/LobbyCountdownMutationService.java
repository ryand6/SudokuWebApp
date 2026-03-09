package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.countdown.CountdownEvaluationResult;
import com.github.ryand6.sudokuweb.domain.lobby.countdown.LobbyCountdownEntity;
import com.github.ryand6.sudokuweb.exceptions.lobby.countdown.LobbyCountdownLockedException;
import jakarta.transaction.Transactional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;


// Handles mutations on Lobby Countdown safely to account for concurrent operations
@Service
public class LobbyCountdownMutationService {

    /**
     * Safely evaluates the countdown with retries on OptimisticLockException.
     * Assumes the countdown entity is already managed in the current transaction.
     */
    @Transactional
    public CountdownEvaluationResult safeEvaluateCountdown(LobbyCountdownEntity countdown) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                return countdown.evaluateCountdownState();
            } catch (ObjectOptimisticLockingFailureException e) {
                attempts++;
            }
        }
        throw new LobbyCountdownLockedException("Failed to evaluate countdown after retries");
    }

    /**
     * Safely resets countdown with retries on OptimisticLockException.
     * Assumes the countdown entity is already managed in the current transaction.
     */
    @Transactional
    public void safeCountdownReset(LobbyCountdownEntity countdown) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                countdown.resetCountdownIfActive();
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                attempts++;
            }
        }
        throw new LobbyCountdownLockedException("Failed to reset countdown after retries");
    }

}
