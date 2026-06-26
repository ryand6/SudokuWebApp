package com.github.ryand6.sudokuweb.domain.game;

import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.settings.GameSettingsEntity;
import com.github.ryand6.sudokuweb.domain.game.state.SharedGameStateEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.*;
import com.github.ryand6.sudokuweb.exceptions.game.IllegalGameStatusChangeException;
import com.github.ryand6.sudokuweb.exceptions.game.player.GameLoadedTimestampNotFoundException;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "games")
public class GameEntity {

    // Allow 200 ms for overhead when initialising game timers
    public static final int GAME_COUNTDOWN_MS = 5200;
    public static final int MAX_WAIT_SECONDS = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lobby_id", nullable = false)
    private LobbyEntity lobbyEntity;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "puzzle_id", nullable = false)
    private SudokuPuzzleEntity sudokuPuzzleEntity;

    @OneToMany(mappedBy = "gameEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GamePlayerEntity> gamePlayerEntities = new HashSet<>();

    @OneToOne(mappedBy = "gameEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private SharedGameStateEntity sharedGameStateEntity;

    @OneToOne(mappedBy = "gameEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private GameSettingsEntity gameSettingsEntity;

    @Column(name = "game_status")
    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus = GameStatus.LOADING;

    // Default start time is max wait seconds after game creation to prevent players being held up by players disconnecting
    @Column(name = "game_starts_at")
    private Instant gameStartsAt = null;

    // End of game timer
    @Column(name = "game_ends_at")
    private Instant gameEndsAt = null;

    // Flag to signal game ended prematurely due to other players forfeiting - allow option for remaining player to continue
    @Column(name = "ended_prematurely")
    private boolean endedPrematurely = false;

    // Timestamp all players have finished
    @Column(name = "game_ended_at")
    private Instant gameEndedAt = null;

    @Version
    private Long version;

    // Overwrite to prevent circular referencing/lazy loading of referenced/nested entities
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameEntity gameEntity)) return false;
        return id != null && id.equals(gameEntity.getId());
    }

    // Overwrite to prevent circular referencing/lazy loading of referenced/nested entities
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    //#######################//
    // Domain Business Logic //
    //#######################//

    @PrePersist
    public void initialiseCreatedAt() {
        createdAt = Instant.now();
    }

    public boolean isBoardStateShared() {
        GameMode gameMode = lobbyEntity.getLobbySettingsEntity().getGameMode();
        return gameMode == GameMode.DOMINATION || gameMode == GameMode.TIMEATTACK;
    }

    public boolean validateInitGameClocksOnLoadedPlayers() {
        return checkAllPlayersLoaded() && gameStatus == GameStatus.LOADING && gameStartsAt == null;
    }

    public GameClocks initGameClocks(boolean playersLoaded) {
        gameStartsAt = playersLoaded ? gamePlayerEntities.stream()
                .max(Comparator.comparing(GamePlayerEntity::getGameLoadedTimestamp))
                .orElseThrow(() -> new GameLoadedTimestampNotFoundException("Game loaded timestamp could not be found for any of the players."))
                .getGameLoadedTimestamp()
                .plusMillis(GAME_COUNTDOWN_MS)
        : Instant.now().plusMillis(GAME_COUNTDOWN_MS);
        if (lobbyEntity != null && lobbyEntity.getLobbySettingsEntity().getTimeLimit() != null && lobbyEntity.getLobbySettingsEntity().getTimeLimit().getSeconds() != null) {
            gameEndsAt = gameStartsAt.plusSeconds(lobbyEntity.getLobbySettingsEntity().getTimeLimit().getSeconds());
        }
        setStatusCountdown();
        return new GameClocks(gameStartsAt, gameEndsAt);
    }

    public void setStatusCountdown() {

        System.out.println("\n\nGame Status when trying to start countdown: " + gameStatus.toString() + "\n\n");

        if (gameStatus == GameStatus.LOADING) {
            gameStatus = GameStatus.COUNTDOWN;
        }
    }

    public void setStatusInProgress() {
        if (gameStatus == GameStatus.COUNTDOWN) {
            gameStatus = GameStatus.IN_PROGRESS;
        } else {
            throw new IllegalGameStatusChangeException("Game status cannot be moved to in progress due to illegal state change.");
        }
    }

    public Set<GamePlayerEntity> getRemainingActivePlayers() {
        return gamePlayerEntities.stream().filter((gp) -> gp.getGameResult() != GameResult.FORFEIT).collect(Collectors.toSet());
    }

    public boolean isAborted(GamePlayerEntity leaveRequester) {
        Set<GamePlayerEntity> remainingPlayers = getRemainingActivePlayers();
        return remainingPlayers.size() == 1 && gamePlayerEntities.contains(leaveRequester);
    }

    public GamePlayerEntity findLastRemainingPlayer() {
        List<GamePlayerEntity> remainingPlayers = getRemainingActivePlayers().stream().toList();
        if (remainingPlayers.size() == 1) {
            return remainingPlayers.get(0);
        }
        return null;
    }

    public void abortGame() {
        gameStatus = GameStatus.ABORTED;
    }

    public void finishGame() {

        System.out.println("\n\nfinishGame() called!\n\n");

        System.out.println("\n\nGame status before marking game as finished: " + gameStatus + "\n\n");

        if (gameStatus == GameStatus.IN_PROGRESS) {
            gameStatus = GameStatus.FINISHED;
            gameEndedAt = Instant.now();
        }  else {
            throw new IllegalGameStatusChangeException("Game status cannot be moved to finished due to illegal state change.");
        }
    }

    public void closeGame() {
        if (gameStatus == GameStatus.FINISHED) {
            gameStatus = GameStatus.CLOSED;
        }  else {
            throw new IllegalGameStatusChangeException("Game status cannot be moved to closed due to illegal state change.");
        }
    }

    private boolean checkAllPlayersLoaded() {
        return gamePlayerEntities.stream().allMatch(GamePlayerEntity::isGameLoaded);
    }

    public List<PlayerColour> getShuffledPlayerColours() {
        // Get list of player colour enums
        PlayerColour[] playerColours = PlayerColour.values();
        List<PlayerColour> colourList = Arrays.asList(playerColours);
        // Randomise order
        Collections.shuffle(colourList);
        return colourList;
    }

    public void addSecondsToGameEndTime(int seconds) {
        gameEndsAt = gameEndsAt.plusSeconds(seconds);
    }

    public void removeSecondsFromGameEndTime(int seconds) {
        seconds = Math.abs(seconds);
        gameEndsAt = gameEndsAt.minusSeconds(seconds).isAfter(Instant.now())
                ? gameEndsAt.minusSeconds(seconds)
                : Instant.now();
    }

    public void reduceEndTimeOnFirstPlayerCompletion() {
        Instant newEndTime = Instant.now().plusSeconds(60);
        if (gameEndsAt.isAfter(newEndTime)) {
            gameEndsAt = newEndTime;
        }
    }

    public boolean validateGameEndedPrematurely() {
        Set<GamePlayerEntity> remainingPlayers = getRemainingActivePlayers();
        return remainingPlayers.size() == 1 && gameSettingsEntity.getGameMode() != GameMode.TIMEATTACK && gameEndsAt.compareTo(Instant.now()) > 0;
    }

    public boolean isGameFinished() {
        return gamePlayerEntities.stream().filter(gp -> gp.getGameResult() != GameResult.FORFEIT).allMatch(GamePlayerEntity::isFinishedGame);
    }

    public Integer determineMaxScore() {
        return gamePlayerEntities.stream().map(GamePlayerEntity::getScore).max(Integer::compare).orElse(0);
    }

    public Set<GamePlayerEntity> determineGameWinners() {
        int maxScore = determineMaxScore();
        return gamePlayerEntities.stream().filter(gp -> gp.getScore() == maxScore).collect(Collectors.toSet());
    }

    public boolean determineTimeAttackVictory() {
        return gameEndedAt.compareTo(gameEndsAt) <= 0;
    }

    public boolean isPlayerFirstToFinish(GamePlayerEntity gamePlayer) {
        Set<GamePlayerEntity> finishedPlayers = gamePlayerEntities.stream().filter(GamePlayerEntity::isFinishedGame).collect(Collectors.toSet());
        return finishedPlayers.size() == 1 && finishedPlayers.contains(gamePlayer);
    }

}
