package com.github.ryand6.sudokuweb.domain.game.settings;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.enums.GameType;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "game_settings")
public class GameSettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_settings_id_seq")
    private Long id;

    @OneToOne
    @JoinColumn(name = "game_id", nullable = false, unique = true)
    private GameEntity gameEntity;

    // Default difficulty = medium
    @Column(name = "difficulty")
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty = Difficulty.MEDIUM;

    // Length a game can last before it ends - default = 30 mins
    @Column(name = "time_limit")
    @Enumerated(EnumType.STRING)
    private TimeLimitPreset timeLimit = TimeLimitPreset.STANDARD;

    // Also present in GameEntity
    @Column(name = "game_mode")
    @Enumerated(EnumType.STRING)
    private GameMode gameMode = GameMode.CLASSIC;

    @Column(name = "game_type")
    @Enumerated(EnumType.STRING)
    private GameType gameType = GameType.RANKED;

    @Version
    private Long version;

}
