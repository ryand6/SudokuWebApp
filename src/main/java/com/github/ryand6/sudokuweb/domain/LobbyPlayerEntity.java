package com.github.ryand6.sudokuweb.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lobby_players")
public class LobbyPlayerEntity {

    @EmbeddedId
    private LobbyPlayerId id;

    @ManyToOne
    @MapsId("lobbyId")
    @JoinColumn(name = "lobby_id", nullable = false)
    private LobbyEntity lobby;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    // set the join time before persisting the entity to the DB for the first time - no manual setting required
    @PrePersist
    public void prePersist() {
        this.joinedAt = Instant.now();
    }

    // Use LobbyPlayerId implementation of equals as the composite key is unique
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LobbyPlayerEntity)) return false;
        LobbyPlayerEntity lobbyPlayerEntity = (LobbyPlayerEntity) o;

        // Compare using only the embedded ID
        return id != null && id.equals(lobbyPlayerEntity.id);
    }

    // Use LobbyPlayerId implementation of hashCode as the composite key is unique
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
