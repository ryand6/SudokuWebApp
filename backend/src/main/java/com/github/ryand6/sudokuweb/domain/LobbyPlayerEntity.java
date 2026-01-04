package com.github.ryand6.sudokuweb.domain;

import com.github.ryand6.sudokuweb.enums.LobbyStatus;
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

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    // Status of player can be "ready", "in game" or "waiting" - determines what is shown in the lobby view
    @Column(name = "lobby_status")
    private LobbyStatus lobbyStatus = LobbyStatus.WAITING;

    // Time of when player became ready to start game
    @Column(name = "ready_at")
    private Instant readyAt = null;

    // Stored the UTC timestamp of the last submitted chat message by the player - used to prevent message spamming
    @Column(name = "lobby_message_timestamp")
    private Instant lobbyMessageTimestamp = null;

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

    // Sets the player's lobby status and either sets the ready time if the status = READY, or removes it
    public void setStatus(LobbyStatus lobbyStatus) {
        this.lobbyStatus = lobbyStatus;
        this.readyAt = (lobbyStatus == LobbyStatus.READY) ? Instant.now() : null;
    }

}
