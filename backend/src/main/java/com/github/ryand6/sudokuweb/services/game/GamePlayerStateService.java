package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.event.GameEventRequest;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerRepository;
import com.github.ryand6.sudokuweb.domain.game.player.state.CellValueAndScoreUpdate;
import com.github.ryand6.sudokuweb.domain.game.player.state.CellValueUpdate;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.game.player.state.TimeAttackUpdate;
import com.github.ryand6.sudokuweb.domain.game.state.CellClaimEvaluationResult;
import com.github.ryand6.sudokuweb.domain.game.state.SharedGameStateEntity;
import com.github.ryand6.sudokuweb.enums.GameEventType;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.events.types.game.*;
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

        applicationEventPublisher.publishEvent(
                new CreateGameLogEvent(gameId, userId, new GameEventRequest(GameEventType.CELL_UPDATE_SUBMISSION, "cell update submitted for row " + row + " and col " + col))
        );

        GamePlayerEntity gamePlayer = getGamePlayerEntity(gameId, userId);
        GamePlayerStateEntity gamePlayerState = gamePlayer.getGamePlayerStateEntity();
        GameMode gameMode = gamePlayer.getGameEntity().getGameMode();
        int cellIndex = gamePlayerState.getCellIndex(row, col);

        if (!isCellUpdateSubmissionStructurallyValid(gamePlayer, row, col)) {

            applicationEventPublisher.publishEvent(
                    new CreateGameLogEvent(gameId, userId, new GameEventRequest(GameEventType.CELL_UPDATE_INVALID, "cell update submission invalid for row " + row + " and col " + col))
            );

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

        applicationEventPublisher.publishEvent(
                new CreateGameLogEvent(gameId, userId, new GameEventRequest(GameEventType.CELL_UPDATE_REJECTED, "cell update submission rejected for row " + row + " and col " + col))
        );

        gamePlayerState.addCellMistake(cellIndex);
        gamePlayer.incrementMistakes();
        int numberOfMistakesOnCell = gamePlayerState.getNumberOfCellMistakes(cellIndex);
        int scoreToBeApplied = determinePenalty(gameMode, numberOfMistakesOnCell);
        gamePlayer.updateScore(scoreToBeApplied);

        if (gameMode == GameMode.TIMEATTACK) {
            int secondsToDeduct = handleTimeAttackTimerDeduction(gamePlayer.getGameEntity());
            applicationEventPublisher.publishEvent(
                    new TimeAttackCellUpdateSubmissionRejectedEvent(gameId, userId, new TimeAttackUpdate(row, col, value, scoreToBeApplied, secondsToDeduct, gamePlayerState.getCurrentStreak()))
            );
        } else {
            applicationEventPublisher.publishEvent(
                    new CellUpdateSubmissionRejectedEvent(gameId, userId, new CellValueAndScoreUpdate(row, col, value, scoreToBeApplied, gamePlayerState.getCurrentStreak()))
            );
        }

        applicationEventPublisher.publishEvent(
                new CreateGameLogEvent(gameId, userId, new GameEventRequest(GameEventType.SCORE_UPDATE, "received a score penalty"))
        );
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

        if (gamePlayer.getGameEntity().isBoardStateShared()) {
            updatedSharedBoardState(gamePlayer.getGameEntity().getSharedGameStateEntity(), cellIndex, value);
        } else {
            updateCurrentBoardState(gamePlayerState, cellIndex, value);
        }

        applicationEventPublisher.publishEvent(
                new CreateGameLogEvent(gameId, userId, new GameEventRequest(GameEventType.CELL_UPDATE_ACCEPTED, "cell update submission accepted for row " + row + " and col " + col))
        );

        boolean hasCellMistakeOccurred = gamePlayerState.hasCellMistakeOccurred(cellIndex);
        CellClaimEvaluationResult cellClaimEvaluationResult = gamePlayer.getGameEntity().getSharedGameStateEntity().evaluateCellClaim(cellIndex, userId, hasCellMistakeOccurred);
        int cellClaimPosition = cellClaimEvaluationResult.getCellClaimPosition();
        handleStreakUpdates(gamePlayerState, gamePlayer, cellClaimEvaluationResult, gameId, userId);
        int scoreToBeApplied = determineScoreToAdd(gameMode, cellClaimPosition, gamePlayerState.getCurrentStreak());
        gamePlayer.updateScore(scoreToBeApplied);

       if (gameMode == GameMode.TIMEATTACK) {
           int secondsToAdd = handleTimeAttackTimerAddition(gamePlayer.getGameEntity());
           applicationEventPublisher.publishEvent(
                   new TimeAttackCellUpdateSubmissionAcceptedEvent(gameId, userId, new TimeAttackUpdate(row, col, value, scoreToBeApplied, secondsToAdd, gamePlayerState.getCurrentStreak()))
           );
       } else {
           applicationEventPublisher.publishEvent(
                   new CellUpdateSubmissionAcceptedEvent(gameId, userId, new CellValueAndScoreUpdate(row, col, value, scoreToBeApplied, gamePlayerState.getCurrentStreak()))
           );
       }

        applicationEventPublisher.publishEvent(
               new CreateGameLogEvent(gameId, userId, new GameEventRequest(GameEventType.SCORE_UPDATE, "score increased"))
        );

       boolean isBoardComplete = gamePlayer.getGameEntity().isBoardStateShared()
               ? gamePlayer.getGameEntity().getSharedGameStateEntity() != null
               && gamePlayer.getGameEntity().getSharedGameStateEntity().isBoardStateComplete()
               : gamePlayerState.getCurrentBoardState() != null
               && gamePlayerState.isBoardStateComplete();

        if (isBoardComplete) {
           applicationEventPublisher.publishEvent(
                   new CreateGameLogEvent(gameId, userId, new GameEventRequest(GameEventType.BOARD_COMPLETED, "completed the board"))
           );
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

           if (gamePlayerState.getCurrentStreak() > 1) {
               applicationEventPublisher.publishEvent(
                       new CreateGameLogEvent(gameId, userId, new GameEventRequest(GameEventType.STREAK_UPDATE, "streak increased"))
               );
           }

       } else {
           gamePlayerState.resetCurrentStreak();
       }
   }

   private int handleTimeAttackTimerDeduction(GameEntity game) {
        int secondsToDeduct = ScoringTables.timeAttackGameMode_RemovedSecondsOnIncorrectAnswer;

        game.removeSecondsFromGameEndTime(secondsToDeduct);

        // IMPLEMENT CALL TO SCHEDULER THAT UPDATES GAME END SCHEDULE

       return secondsToDeduct;
   }

    private int handleTimeAttackTimerAddition(GameEntity game) {
        int secondsToAdd = ScoringTables.timeAttackGameMode_AddedSecondsOnCorrectAnswer;

        game.addSecondsToGameEndTime(secondsToAdd);

        // IMPLEMENT CALL TO SCHEDULER THAT UPDATES GAME END SCHEDULE

        return secondsToAdd;
    }

    int determinePenalty(GameMode gameMode, Integer numberOfMistakesOnCell) {
        int scoreToBeApplied = 0;
        switch (gameMode) {
            case CLASSIC -> {
                scoreToBeApplied = determineStandardGameModePenalty(numberOfMistakesOnCell);
            }
            case DOMINATION -> {
                scoreToBeApplied = determineDominationGameModePenalty();
            }
            case TIMEATTACK -> {
                // No score penalty, only timer penalty
            }
        }
        return scoreToBeApplied;
    }

    int determineScoreToAdd(GameMode gameMode, int cellClaimPosition, int currentStreak) {
        int scoreToBeApplied = 0;
        switch (gameMode) {
            case CLASSIC -> {
                scoreToBeApplied = determineStandardGameModeScoreToAdd(cellClaimPosition, currentStreak);
            }
            case DOMINATION -> {
                scoreToBeApplied = determineDominationGameModeScoreToAdd(currentStreak);
            }
            case TIMEATTACK -> {
                scoreToBeApplied = determineTimeAttackGameModeScoreToAdd();
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

    int determineDominationGameModePenalty() {
        return ScoringTables.dominationGameMode_BasePenalty;
    }

    int determineDominationGameModeScoreToAdd(int currentStreak) {
        int baseScore = ScoringTables.dominationGameMode_BaseScore;
        int streakMultiplier = Math.min(currentStreak, ScoringTables.dominationGameMode_StreakBonusCap);
        Integer bonusScore = ScoringTables.dominationGameMode_BonusPointsPerStreak.get(streakMultiplier);
        return bonusScore != null ? baseScore + bonusScore : baseScore;
    }

    int determineTimeAttackGameModeScoreToAdd() {
        return ScoringTables.timeAttackGameMode_BaseScore;
    }

    void resetPlayersStreak(Long gameId, Long userId) {
        GamePlayerEntity gamePlayer = getGamePlayerEntity(gameId, userId);
        GamePlayerStateEntity gamePlayerState = gamePlayer.getGamePlayerStateEntity();

        if (gamePlayerState.getCurrentStreak() > 1) {
            applicationEventPublisher.publishEvent(
                    new CreateGameLogEvent(gameId, userId, new GameEventRequest(GameEventType.STREAK_UPDATE, "streak reset"))
            );
        }

        gamePlayerState.resetCurrentStreak();
    }

    @Transactional
    void updateCurrentBoardState(GamePlayerStateEntity gamePlayerState, int cellIndex, int value) {
        gamePlayerState.updateCurrentBoardState(cellIndex, Character.forDigit(value, 10));
    }

    @Transactional
    void updatedSharedBoardState(SharedGameStateEntity sharedGameState, int cellIndex, int value) {
        sharedGameState.updateCurrentBoardState(cellIndex, Character.forDigit(value, 10));
    }

    boolean isCellUpdateSubmissionStructurallyValid(GamePlayerEntity gamePlayer, int row, int col) {
        String initialBoardState = gamePlayer.getGameEntity().getSudokuPuzzleEntity().getInitialBoardState();
        String currentBoardState = gamePlayer.getGamePlayerStateEntity().getCurrentBoardState() != null
                ? gamePlayer.getGamePlayerStateEntity().getCurrentBoardState()
                : gamePlayer.getGameEntity().getSharedGameStateEntity().getCurrentSharedBoardState();
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
