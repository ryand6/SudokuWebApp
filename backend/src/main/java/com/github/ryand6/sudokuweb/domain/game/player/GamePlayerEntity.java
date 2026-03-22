package com.github.ryand6.sudokuweb.domain.game.player;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.player.settings.GamePlayerSettingsEntity;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.GameResult;
import com.github.ryand6.sudokuweb.enums.GameStatus;
import com.github.ryand6.sudokuweb.enums.PlayerColour;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "game_players")
public class GamePlayerEntity {

    @EmbeddedId
    private GamePlayerId id = new GamePlayerId();

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity gameEntity;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @OneToOne(mappedBy = "gamePlayerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private GamePlayerStateEntity gamePlayerStateEntity;

    @OneToOne(mappedBy = "gamePlayerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private GamePlayerSettingsEntity gamePlayerSettingsEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "player_colour", nullable = false)
    private PlayerColour playerColour;

    @Column(name = "score")
    private int score = 0;

    // Counts the number of times a player answered a cell without making a mistake before any other player
    @Column(name = "firsts")
    private int firsts = 0;

    @Column(name = "mistakes")
    private int mistakes = 0;

    @Column(name = "game_loaded")
    private boolean gameLoaded = false;

    @Column(name = "game_loaded_timestamp")
    private Instant gameLoadedTimestamp = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_result", nullable = false)
    private GameResult gameResult = GameResult.PENDING;

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GamePlayerEntity)) return false;
        GamePlayerEntity gamePlayerEntity = (GamePlayerEntity) o;

        // Compare using the two linked entities that make up the unique identifier
        return Objects.equals(gameEntity, gamePlayerEntity.gameEntity) &&
                Objects.equals(userEntity, gamePlayerEntity.userEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameEntity, userEntity);
    }

    //#######################//
    // Domain Business Logic //
    //#######################//

    public boolean canGameResultBeUpdated() {
        return gameEntity.getGameStatus() == GameStatus.FINISHED;
    }

    public void markGameLoaded() {
        if (!gameLoaded && gameLoadedTimestamp == null) {
            gameLoaded = true;
            gameLoadedTimestamp = Instant.now();
        }
    }

    public void incrementFirsts() {
        firsts += 1;
    }

    public void incrementMistakes() {
        mistakes += 1;
    }

}
