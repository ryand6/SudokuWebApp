package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerRepository;
import com.github.ryand6.sudokuweb.domain.game.player.state.CellValueUpdate;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.game.state.CellClaimEvaluationResult;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.events.types.game.CellUpdateSubmissionAcceptedEvent;
import com.github.ryand6.sudokuweb.events.types.game.CellUpdateSubmissionInvalidEvent;
import com.github.ryand6.sudokuweb.events.types.game.CellUpdateSubmissionRejectedEvent;
import com.github.ryand6.sudokuweb.exceptions.game.player.GamePlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.game.state.GamePlayerStateOptimisticLockException;
import com.github.ryand6.sudokuweb.util.ScoringTables;
import com.github.ryand6.sudokuweb.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class GamePlayerStateService {

    private final GamePlayerRepository gamePlayerRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public GamePlayerStateService(GamePlayerRepository gamePlayerRepository,
                                  ApplicationEventPublisher applicationEventPublisher) {
        this.gamePlayerRepository = gamePlayerRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @Transactional
    public void handleCellUpdateSubmission(Long gameId, Long userId, int row, int col, int value) {

        // IMPLEMENT CALL TO GAME EVENT CREATION - CELL UPDATE SUBMISSION

        GamePlayerEntity gamePlayer = getGamePlayerEntity(gameId, userId);
        GamePlayerStateEntity gamePlayerState = gamePlayer.getGamePlayerStateEntity();
        GameMode gameMode = gamePlayer.getGameEntity().getGameMode();
        int cellIndex = gamePlayerState.getCellIndex(row, col);

        if (!isCellUpdateSubmissionStructurallyValid(gamePlayer, row, col, value)) {
            // IMPLEMENT CALL TO GAME EVENT CREATION - CELL UPDATE INVALID
            applicationEventPublisher.publishEvent(
                    new CellUpdateSubmissionInvalidEvent(gameId, userId, new CellValueUpdate(row, col, value))
            );
            return;
        }
        if (!isCellUpdateSubmissionCorrect(gamePlayer, row, col, value)) {
            handleIncorrectSubmission(gamePlayerState, gamePlayer, cellIndex, gameMode, gameId, userId, row, col, value);
            return;
        }
        handleCorrectSubmission(gamePlayerState, gamePlayer, cellIndex, gameMode, gameId, userId, row, col, value);
    }

    @Recover
    public void handleCellUpdateSubmissionRecover(ObjectOptimisticLockingFailureException ex, Long gameId, Long userId, int row, int col, int value) {
        throw new GamePlayerStateOptimisticLockException("Unable to apply cell update due to a conflict.");
    }

    private void handleIncorrectSubmission(
            GamePlayerStateEntity gamePlayerState,
            GamePlayerEntity gamePlayer,
            int cellIndex,
            GameMode gameMode,
            Long gameId,
            Long userId,
            int row,
            int col,
            int value
    ) {
        // IMPLEMENT CALL TO GAME EVENT CREATION - CELL UPDATE REJECTED
        gamePlayerState.addCellMistake(cellIndex);
        gamePlayer.incrementMistakes();
        int numberOfMistakesOnCell = gamePlayerState.getNumberOfCellMistakes(cellIndex);
        int scoreToBeApplied = determinePenalty(gameMode, numberOfMistakesOnCell);
        gamePlayer.updateScore(scoreToBeApplied);
        applicationEventPublisher.publishEvent(
                new CellUpdateSubmissionRejectedEvent(gameId, userId, new CellValueUpdate(row, col, value))
        );

        // IMPLEMENT WS EVENT FOR SCORE UPDATE

    }

   private void handleCorrectSubmission(
           GamePlayerStateEntity gamePlayerState,
           GamePlayerEntity gamePlayer,
           int cellIndex,
           GameMode gameMode,
           Long gameId,
           Long userId,
           int row,
           int col,
           int value
   ) {
       updateCurrentBoardState(gamePlayerState, cellIndex, value);
       // IMPLEMENT CALL TO GAME EVENT CREATION - CELL UPDATE ACCEPTED
       applicationEventPublisher.publishEvent(
               new CellUpdateSubmissionAcceptedEvent(gameId, userId, new CellValueUpdate(row, col, value))
       );

       boolean hasCellMistakeOccurred = gamePlayerState.hasCellMistakeOccurred(cellIndex);
       CellClaimEvaluationResult cellClaimEvaluationResult = gamePlayer.getGameEntity().getSharedGameStateEntity().evaluateCellClaim(cellIndex, userId, hasCellMistakeOccurred);
       int cellClaimPosition = cellClaimEvaluationResult.getCellClaimPosition();
       handleStreakUpdates(gamePlayerState, gamePlayer, cellClaimEvaluationResult, gameId, userId);
       int scoreToBeApplied = determineScoreToAdd(gameMode, cellClaimPosition, gamePlayerState.getCurrentStreak());
       gamePlayer.updateScore(scoreToBeApplied);
       // IMPLEMENT WS EVENT FOR SCORE UPDATE

       // IMPLEMENT CALL TO SCORE, STREAK AND MULTIPLIER UPDATE
       if (gamePlayer.getGamePlayerStateEntity().isBoardStateComplete()) {
           // IMPLEMENT CALL TO GAME EVENT CREATION - BOARD COMPLETE
           // IMPLEMENT CALL TO SCORE UPDATE
           // IMPLEMENT CALL TO GAME FINISH HANDLER (if applicable)
       }
   }

   private void handleStreakUpdates(
           GamePlayerStateEntity gamePlayerState,
           GamePlayerEntity gamePlayer,
           CellClaimEvaluationResult cellClaimEvaluationResult,
           Long gameId,
           Long userId
   ) {
       if (cellClaimEvaluationResult.getCellClaimPosition() == 1) {
           gamePlayer.incrementFirsts();
           gamePlayerState.incrementCurrentStreak();
           gamePlayer.setMaxStreak(gamePlayerState.getCurrentStreak());
           Long previousFirstWinnerId = cellClaimEvaluationResult.getPreviousFirstWinner();
           if (previousFirstWinnerId != null && !previousFirstWinnerId.equals(userId)) {
               resetPlayersStreak(gameId, previousFirstWinnerId);
           }
       } else {
           gamePlayerState.resetCurrentStreak();
       }
   }

    int determinePenalty(GameMode gameMode, Integer numberOfMistakesOnCell) {
        int scoreToBeApplied = 0;
        switch (gameMode) {
            case CLASSIC: {
                scoreToBeApplied = determineStandardGameModePenalty(numberOfMistakesOnCell);
                break;
            }
            case DOMINATION: {
                // IMPLEMENT
                break;
            }
            case TIMEATTACK: {
                // IMPLEMENT
                break;
            }
        }
        return scoreToBeApplied;
    }

    int determineScoreToAdd(GameMode gameMode, int cellClaimPosition, int currentStreak) {
        int scoreToBeApplied = 0;
        switch (gameMode) {
            case CLASSIC: {
                scoreToBeApplied = determineStandardGameModeScoreToAdd(cellClaimPosition, currentStreak);
                break;
            }
            case DOMINATION: {
                // IMPLEMENT
                break;
            }
            case TIMEATTACK: {
                // IMPLEMENT
                break;
            }
        }
        return scoreToBeApplied;
    }

    int determineStandardGameModePenalty(Integer numberOfMistakesOnCell) {
        if (numberOfMistakesOnCell == null || numberOfMistakesOnCell <= 0) return 0;
        int penaltyMultiplier = Math.min(numberOfMistakesOnCell, ScoringTables.standardGameMode_PenaltyMultiplierCap);
        return ScoringTables.standardGameMode_Penalties.get(penaltyMultiplier);
    }

    int determineStandardGameModeScoreToAdd(int claimPosition, int currentStreak) {
        int baseScore = ScoringTables.standardGameMode_PointsPerClaimPosition.get(claimPosition);
        int streakMultiplier = Math.min(currentStreak, ScoringTables.standardGameMode_StreakBonusCap);
        Integer bonusScore = ScoringTables.standardGameMode_BonusPointsPerStreak.get(streakMultiplier);
        return bonusScore != null ? baseScore + bonusScore : baseScore;
    }

    void resetPlayersStreak(Long gameId, Long userId) {
        GamePlayerEntity gamePlayer = getGamePlayerEntity(gameId, userId);
        gamePlayer.getGamePlayerStateEntity().resetCurrentStreak();
    }

    @Transactional
    void updateCurrentBoardState(GamePlayerStateEntity gamePlayerState, int cellIndex, int value) {
        gamePlayerState.updateCurrentBoardState(cellIndex, Character.forDigit(value, 10));
    }

    boolean isCellUpdateSubmissionStructurallyValid(GamePlayerEntity gamePlayer, int row, int col, int value) {
        String initialBoardState = gamePlayer.getGameEntity().getSudokuPuzzleEntity().getInitialBoardState();
        String currentBoardState = gamePlayer.getGamePlayerStateEntity().getCurrentBoardState();
        if (currentBoardState == null) return false;
        // Number already exists
        if (StringUtils.getBoardStateValueFromNestedArrayIndexes(initialBoardState, row, col) != '.') {
            return false;
        } else if (StringUtils.getBoardStateValueFromNestedArrayIndexes(currentBoardState, row, col) != '.') {
            return false;
        }
        return true;
    }

    boolean isCellUpdateSubmissionCorrect(GamePlayerEntity gamePlayer, int row, int col, int value) {
        String solution = gamePlayer.getGameEntity().getSudokuPuzzleEntity().getSolution();
        char valueChar = Character.forDigit(value, 10);
        return StringUtils.getBoardStateValueFromNestedArrayIndexes(solution, row, col) == valueChar;
    }

    public GamePlayerEntity getGamePlayerEntity(Long gameId, Long userId) {
        return gamePlayerRepository.findByCompositeId(gameId, userId)
                .orElseThrow(() -> new GamePlayerNotFoundException( "Game Player with Game ID " + gameId + " and User ID " + userId + " does not exist"));
    }

}
