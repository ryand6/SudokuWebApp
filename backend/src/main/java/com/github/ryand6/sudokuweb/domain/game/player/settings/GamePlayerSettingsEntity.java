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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_player_settings_id_seq")
    private Long id;

    @OneToOne
    @JoinColumn(name = "game_player_id", nullable = false, unique = true)
    private GamePlayerEntity gamePlayerEntity;

    @Column(name = "show_rival_highlighted_squares")
    private boolean showRivalHighlightedSquares = true;

}
