package com.github.ryand6.sudokuweb.domain;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lobbies")
public class LobbyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lobby_id_seq")
    private Long id;

    @Column(name = "lobby_name", nullable = false)
    private String lobbyName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // Default difficulty = medium
    @Column(name = "difficulty")
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty = Difficulty.MEDIUM;

    // Length a game can last before it ends - default = 30 mins
    @Column(name = "time_limit")
    private TimeLimitPreset timeLimit = TimeLimitPreset.STANDARD;

    // true if lobby open, false if no players active in the lobby anymore
    @Column(name = "is_active")
    private Boolean isActive;

    // true for public lobby, false for private
    @Column(name = "is_public")
    private Boolean isPublic;

    // true if a game is currently active, meaning users can't join the lobby at this time
    @Column(name = "in_game")
    private Boolean inGame;

    // code for joining the code (if private)
    @Column(name = "join_code", unique = true)
    private String joinCode;

    // Countdown system for starting games
    @Column(name = "countdown_active")
    private Boolean countdownActive = false;

    // Used to determine the time the game will start, unless the host cancels the countdown
    @Column(name = "countdownEndsAt")
    private Instant countdownEndsAt;

    // User ID who started the countdown - host can stop countdown if they triggered it, but if triggered by users readying up, can't be stopped
    @Column(name = "countdown_initiated_by")
    private Long countdownInitiatedBy;

    // Settings lock (prevents changes during countdown)
    @Column(name = "settings_locked")
    private Boolean settingsLocked = false;

    // FetchType.EAGER as only up to 4x LobbyPlayers are linked at any one time
    // Initialise HashSet to prevent null errors as the field will not be initialised until after the LobbyEntity is created
    // This is because LobbyPlayerEntity can only be created once LobbyEntity is persisted
    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<LobbyPlayerEntity> lobbyPlayers = new HashSet<>();

    // Reference user id of the host - user that set up the lobby, or the earliest person in
    // the current set of active users that joined the lobby (if previous host left)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "host_user_id", nullable = false)
    private UserEntity host;

    @OneToMany(mappedBy = "lobbyEntity", cascade = CascadeType.REMOVE)
    private Set<GameEntity> gameEntities;

    // Overwrite to prevent circular referencing/lazy loading of referenced entities e.g. Users
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyEntity lobbyEntity = (LobbyEntity) o;
        return id != null && id.equals(lobbyEntity.id);
    }

    // Overwrite to prevent circular referencing/lazy loading of referenced entities e.g. Users
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean isCountdownActive() {
        return countdownActive != null && countdownActive && countdownEndsAt != null && countdownEndsAt.isAfter(Instant.now());
    }

    // There must be at least 3x players to initiate a countdown due to 2/3 minimum threshold
    // Countdown must not already be active
    public boolean canPlayersInitiateCountdown() {
        return !isCountdownActive() && lobbyPlayers.size() > 2;
    }

    // Used for various scenarios e.g. countdown has ended due to host cancelling or game starting
    public void resetCountdownData() {
        countdownActive = false;
        countdownEndsAt = null;
        countdownInitiatedBy = null;
        settingsLocked = false;
    }

}
