package com.github.ryand6.sudokuweb.domain;

import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lobby_countdowns")
public class LobbyCountdownEntity {

    // Used to allow testing of time related functions
    @Builder.Default
    @Transient
    private Clock clock = Clock.systemUTC();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lobby_countdown_id_seq")
    private Long id;

    @OneToOne
    @JoinColumn(name = "lobby_id", nullable = false, unique = true)
    private LobbyEntity lobbyEntity;

    // Countdown system for starting games
    @Column(name = "countdown_active")
    private boolean countdownActive = false;

    // Used to determine the time the game will start, unless the host cancels the countdown
    @Column(name = "countdown_ends_at")
    private Instant countdownEndsAt = null;

    // User ID who started the countdown - host can stop countdown if they triggered it, but if triggered by users readying up, can't be stopped
    @Column(name = "countdown_initiated_by")
    private Long countdownInitiatedBy = null;

    @Version
    private Long version;

    public boolean isCountdownActive() {
        return countdownActive && countdownEndsAt != null && countdownEndsAt.isAfter(Instant.now(clock));
    }

    // Method used to determine if and when the countdown should be initiated
    // Returns the ID of the new initiator if exists
    public Optional<Long> evaluateCountdownState() {
        int playerCount = lobbyEntity.getLobbyPlayers().size();
        // Must be at least two players in a lobby to start countdown
        if (playerCount < 2) {
            resetCountdownIfActive();
            return Optional.empty();
        }

        // Track number of players that are ready - determines the length of the countdown timer
        long readyCount = lobbyEntity.getLobbyPlayers().stream()
                .filter(lp -> lp.getLobbyStatus() == LobbyStatus.READY)
                .count();

        // Get the lobby host
        LobbyPlayerEntity hostPlayer = lobbyEntity.findHostPlayer();

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
            return Optional.empty();
        }

        Optional<Long> newInitiator = determineCountdownInitiator(hostReady, hostPlayer);

        newInitiator.ifPresent(id -> {
            countdownInitiatedBy = id;
            setCountdown(notReadyCount);
        });

        return newInitiator;
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
    }

    // If the host has readied up, they are always the initiator, otherwise it is the last person to ready up out of the majority
    private Optional<Long> determineCountdownInitiator(boolean hostReady, LobbyPlayerEntity hostPlayer) {
        return hostReady
                ? Optional.of(hostPlayer.getUser().getId())
                : lobbyEntity.getLobbyPlayers().stream()
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
    }

}
