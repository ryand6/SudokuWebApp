package com.github.ryand6.sudokuweb.domain;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import com.github.ryand6.sudokuweb.exceptions.lobby.LobbyHostNotFoundException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Clock;
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

    // true if lobby open, false if no players active in the lobby anymore
    @Column(name = "is_active")
    private boolean isActive;

    // true if players are currently in the middle of a Sudoku game
    @Column(name = "in_game")
    private boolean inGame = false;

    @Column(name = "current_game_id")
    private Long currentGameId = null;

    @OneToOne(mappedBy = "lobbyEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private LobbySettingsEntity lobbySettingsEntity;

    @OneToOne(mappedBy = "lobbyEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private LobbyCountdownEntity lobbyCountdownEntity;

    @Version
    private Long version;

    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<LobbyPlayerEntity> lobbyPlayers = new HashSet<>();

    // Reference user id of the host - user that set up the lobby, or the earliest person in
    // the current set of active users that joined the lobby (if previous host left)
    @ManyToOne(fetch = FetchType.LAZY)
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

    public LobbyPlayerEntity findHostPlayer() {
        // Get the lobby host
        return lobbyPlayers.stream()
                .filter(lp -> lp.getUser().getId().equals(host.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new LobbyHostNotFoundException(
                                "Lobby Host with ID " + host.getId() + " does not exist as a Lobby Player"
                        ));
    }

}
