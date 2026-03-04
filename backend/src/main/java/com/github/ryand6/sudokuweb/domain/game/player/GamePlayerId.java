package com.github.ryand6.sudokuweb.domain.game.player;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

// Composite key used as unique ID for GamePlayerEntity
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GamePlayerId implements Serializable {

    private Long gameId;
    private Long userId;

}
