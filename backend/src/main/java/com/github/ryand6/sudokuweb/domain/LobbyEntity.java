package com.github.ryand6.sudokuweb.domain;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import com.github.ryand6.sudokuweb.exceptions.LobbyHostNotFoundException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lobbies")
public class LobbyEntity {

    public static final int LOBBY_SIZE = 4;

    // Used to allow testing of time related functions
    @Builder.Default
    @Transient
    private Clock clock = Clock.systemUTC();

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

    // true if players are currently in the middle of a Sudoku game
    @Column(name = "in_game")
    private Boolean inGame = false;

    @Column(name = "current_game_id")
    private Long currentGameId = null;

    // Countdown system for starting games
    @Column(name = "countdown_active")
    private Boolean countdownActive = false;

    // Used to determine the time the game will start, unless the host cancels the countdown
    @Column(name = "countdown_ends_at")
    private Instant countdownEndsAt = null;

    // User ID who started the countdown - host can stop countdown if they triggered it, but if triggered by users readying up, can't be stopped
    @Column(name = "countdown_initiated_by")
    private Long countdownInitiatedBy = null;

    // Settings lock (prevents changes during countdown)
    @Column(name = "settings_locked")
    private Boolean settingsLocked = false;

    // FetchType.EAGER as only up to 4x LobbyPlayers are linked at any one time
    // Initialise HashSet to prevent null errors as the field will not be initialised until after the LobbyEntity is created
    // This is because LobbyPlayerEntity can only be created once LobbyEntity is persisted
    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
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
        return countdownActive != null && countdownActive && countdownEndsAt != null && countdownEndsAt.isAfter(Instant.now(clock));
    }

    // Return the entity record of the new host based on the order in which players joined
    public Optional<UserEntity> determineNextHost() {
        UserEntity currentHost = getHost();
        Set<LobbyPlayerEntity> activePlayers = getLobbyPlayers();
        return activePlayers.stream()
                // Remove current host from the stream
                .filter(lp -> !lp.getUser().equals(currentHost))
                // Order by join date/time from first to last
                .sorted(Comparator.comparing(LobbyPlayerEntity::getJoinedAt))
                // Transform to list of User entities
                .map(LobbyPlayerEntity::getUser)
                // Return the player who joined first to be the new host
                .findFirst();
    }

    // Method used to determine if and when the countdown should be initiated
    // Returns the ID of the new initiator if exists
    public Optional<Long> evaluateCountdownState() {
        int playerCount = lobbyPlayers.size();
        // Must be at least two players in a lobby to start countdown
        if (playerCount < 2) {
            resetCountdownIfActive();
            return Optional.empty();
        }

        // Track number of players that are ready - determines the length of the countdown timer
        long readyCount = lobbyPlayers.stream()
                .filter(lp -> lp.getLobbyStatus() == LobbyStatus.READY)
                .count();

        // Get the lobby host
        LobbyPlayerEntity hostPlayer = findHostPlayer();

        // First of two potential conditions that can initiate a countdown
        boolean hostReady = hostPlayer.getLobbyStatus() == LobbyStatus.READY;

        // Second countdown initiating condition - majority of players ready in lobby sizes of 3 or more
        boolean majorityReady = playerCount > 2 && (readyCount >= playerCount - 1);

        boolean shouldCountdownBeActive = hostReady || majorityReady;

        // Reset any active countdown if neither of the required conditions are met
        if (!shouldCountdownBeActive) {
            resetCountdownIfActive();
            return Optional.empty();
        }

        int notReadyCount = playerCount - (int) readyCount;

        // Countdown already started, only update the countdown
        if (countdownInitiatedBy != null) {
            setCountdown(notReadyCount);
        }

        Optional<Long> newInitiator = determineCountdownInitiator(hostReady, hostPlayer);

        newInitiator.ifPresent(id -> {
            countdownInitiatedBy = id;
            setCountdown(notReadyCount);
        });

        return newInitiator;
    }

    private LobbyPlayerEntity findHostPlayer() {
        // Get the lobby host
        return lobbyPlayers.stream()
                .filter(lp -> lp.getUser().getId().equals(host.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new LobbyHostNotFoundException(
                                "Lobby Host with ID " + host.getId() + " does not exist as a Lobby Player"
                        ));
    }

    // Used to initiate or update countdown and set necessary settings - if more players ready up, the countdown is updated
    private void setCountdown(int notReadyCount) {
        Instant timerEnd = Instant.now(clock);
        // Calculate the number of minutes for the countdown timer
        if (notReadyCount >= 1) {
            // Add a minute for every non-ready user
            timerEnd = timerEnd.plus(Duration.ofMinutes(notReadyCount));
        }

        countdownActive = true;
        countdownEndsAt = timerEnd;
        settingsLocked = true;
    }

    // If the host has readied up, they are always the initiator, otherwise it is the last person to ready up out of the majority
    private Optional<Long> determineCountdownInitiator(boolean hostReady, LobbyPlayerEntity hostPlayer) {
        return hostReady
                ? Optional.of(hostPlayer.getUser().getId())
                : lobbyPlayers.stream()
                .filter(lp -> lp.getLobbyStatus() == LobbyStatus.READY)
                // Last player to ready up
                .max(Comparator.comparing(LobbyPlayerEntity::getReadyAt))
                .map(lp -> lp.getUser().getId());
    }

    // Helper to reset countdown only if it is active
    public void resetCountdownIfActive() {
        if (isCountdownActive()) {
            resetCountdownData();
        }
    }

    // Used for various scenarios e.g. countdown has ended due to host cancelling or game starting
    private void resetCountdownData() {
        countdownActive = false;
        countdownEndsAt = null;
        countdownInitiatedBy = null;
        settingsLocked = false;
    }

}
