package com.github.ryand6.sudokuweb.domain;

import com.github.ryand6.sudokuweb.enums.PreferenceDirection;
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

    // Determine whether player in lobby is ready to start the game
    @Column(name = "is_ready")
    private Boolean isReady = false;

    // Time of when player became ready to start game
    @Column(name = "ready_at")
    private Instant readyAt;

    // Player preference for game difficulty, used in Lobby polling window - null until preference vote cast
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_preference")
    private PreferenceDirection difficultyPreference = null;

    // Stored the UTC timestamp of the last submitted vote by the player for the difficulty setting
    @Column(name = "difficulty_vote_timestamp")
    private Instant difficultyVoteTimestamp = null;

    // Player preference for game duration, used in Lobby polling window - null until preference vote cast
    @Enumerated(EnumType.STRING)
    @Column(name = "duration_preference")
    private PreferenceDirection durationPreference = null;

    // Stored the UTC timestamp of the last submitted vote by the player for the duration setting
    @Column(name = "duration_vote_timestamp")
    private Instant durationVoteTimestamp = null;

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

    // Sets the player's ready status and either sets the ready time or removes it depending on status
    public void setReady(boolean ready) {
        this.isReady = ready;
        this.readyAt = ready ? Instant.now() : null;
    }

}
