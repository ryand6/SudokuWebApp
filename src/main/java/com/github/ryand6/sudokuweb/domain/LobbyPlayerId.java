package com.github.ryand6.sudokuweb.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

// Composite key used as unique ID for LobbyPlayerEntity
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LobbyPlayerId implements Serializable {

    private Long lobbyId;
    private Long userId;

}
