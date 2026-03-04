package com.github.ryand6.sudokuweb.domain.game.player;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.player.settings.GamePlayerSettingsEntity;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.GameResult;
import com.github.ryand6.sudokuweb.enums.PlayerColour;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "game_players")
public class GamePlayerEntity {

    @EmbeddedId
    private GamePlayerId id;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity gameEntity;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToOne(mappedBy = "gamePlayerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private GamePlayerStateEntity gamePlayerStateEntity;

    @OneToOne(mappedBy = "gamePlayerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private GamePlayerSettingsEntity gamePlayerSettingsEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "player_colour", nullable = false)
    private PlayerColour playerColour;

    @Column(name = "score")
    private int score = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_result", nullable = false)
    private GameResult gameResult = GameResult.PENDING;

    // Use GamePlayerId implementation of equals as the composite key is unique
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GamePlayerEntity)) return false;
        GamePlayerEntity gamePlayerEntity = (GamePlayerEntity) o;

        // Compare using only the embedded ID
        return id != null && id.equals(gamePlayerEntity.id);
    }

    // Use GamePlayerId implementation of hashCode as the composite key is unique
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
