package com.github.ryand6.sudokuweb.domain;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lobby_settings")
public class LobbySettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lobby_settings_id_seq")
    private Long id;

    @OneToOne
    @JoinColumn(name = "lobby_id", nullable = false, unique = true)
    private LobbyEntity lobbyEntity;

    // true for public lobby, false for private
    @Column(name = "is_public")
    private boolean isPublic;

    // Default difficulty = medium
    @Column(name = "difficulty")
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty = Difficulty.MEDIUM;

    // Length a game can last before it ends - default = 30 mins
    @Column(name = "time_limit")
    @Enumerated(EnumType.STRING)
    private TimeLimitPreset timeLimit = TimeLimitPreset.STANDARD;

    @Version
    private Long version;

}
