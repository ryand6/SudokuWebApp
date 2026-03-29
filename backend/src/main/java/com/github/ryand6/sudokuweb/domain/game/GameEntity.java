package com.github.ryand6.sudokuweb.domain.game;

import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.state.SharedGameStateEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.enums.GameStatus;
import com.github.ryand6.sudokuweb.enums.PlayerColour;
import com.github.ryand6.sudokuweb.exceptions.game.IllegalGameStatusChangeException;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "games")
public class GameEntity {

    public static final int GAME_COUNTDOWN_SECONDS = 5;
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

    @Column(name = "mode")
    @Enumerated(EnumType.STRING)
    private GameMode gameMode = GameMode.CLASSIC;

    @Column(name = "game_status")
    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus = GameStatus.LOADING;

    // Default start time is max wait seconds after game creation to prevent players being held up by players disconnecting
    @Column(name = "game_starts_at")
    private Instant gameStartsAt = null;

    @Column(name = "game_ends_at")
    private Instant gameEndsAt = null;

    @Version
    private Long version;

    // Overwrite to prevent circular referencing/lazy loading of referenced/nested entities
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameEntity gameEntity = (GameEntity) o;
        return id != null && id.equals(gameEntity.id);
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
    public void initialiseGameClocks() {
        createdAt = Instant.now();

        gameStartsAt = createdAt
                .plusSeconds(MAX_WAIT_SECONDS)
                .plusSeconds(GAME_COUNTDOWN_SECONDS);

        if (lobbyEntity != null && lobbyEntity.getLobbySettingsEntity().getTimeLimit() != null) {
            gameEndsAt = gameStartsAt.plusSeconds(
                    lobbyEntity.getLobbySettingsEntity()
                            .getTimeLimit()
                            .getSeconds()
            );
        }
    }

    public boolean isBoardStateShared() {
        GameMode gameMode = lobbyEntity.getLobbySettingsEntity().getGameMode();
        return gameMode == GameMode.DOMINATION || gameMode == GameMode.TIMEATTACK;
    }

    public GameLoadEvaluationResult updateGameClocks() {
        if (checkAllPlayersLoaded()
                && Instant.now().isBefore(gameStartsAt)
                && gameStatus == GameStatus.LOADING) {
            gameStartsAt = gamePlayerEntities.stream()
                    .max(Comparator.comparing(GamePlayerEntity::getGameLoadedTimestamp))
                    .orElseThrow()
                    .getGameLoadedTimestamp()
                    .plusSeconds(GAME_COUNTDOWN_SECONDS);
            if (lobbyEntity != null && lobbyEntity.getLobbySettingsEntity().getTimeLimit() != null) {
                gameEndsAt = gameStartsAt.plusSeconds(lobbyEntity.getLobbySettingsEntity().getTimeLimit().getSeconds());
            }
            gameStatus = GameStatus.COUNTDOWN;
            return new GameLoadEvaluationResult(gameStartsAt, gameEndsAt);
        }
        return null;
    }

    public void setStatusInProgress() {
        if (gameStatus == GameStatus.COUNTDOWN) {
            gameStatus = GameStatus.IN_PROGRESS;
        } else {
            throw new IllegalGameStatusChangeException("Game status cannot be moved to in progress due to illegal state change.");
        }
    }

    public boolean isAborted(GamePlayerEntity leaveRequester) {
        return gamePlayerEntities.size() == 1 && gamePlayerEntities.contains(leaveRequester);
    }

    public void abortGame() {
        gameStatus = GameStatus.ABORTED;
    }

    public void finishGame() {
        gameStatus = GameStatus.FINISHED;
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

}
