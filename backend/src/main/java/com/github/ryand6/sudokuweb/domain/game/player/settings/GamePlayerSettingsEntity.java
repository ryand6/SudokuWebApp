package com.github.ryand6.sudokuweb.domain.game.player.settings;


import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerId;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "game_player_settings")
public class GamePlayerSettingsEntity {

    @EmbeddedId
    private GamePlayerId id; // same composite key as GamePlayerEntity

    @MapsId
    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "game_id", referencedColumnName = "game_id", nullable = false),
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    })
    private GamePlayerEntity gamePlayerEntity;

    @Column(name = "show_rival_highlighted_squares")
    private boolean showOtherPlayerHighlightedSquares = true;

}
