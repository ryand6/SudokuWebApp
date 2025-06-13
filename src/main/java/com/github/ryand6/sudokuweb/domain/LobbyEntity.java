package com.github.ryand6.sudokuweb.domain;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;

    @Column(name = "difficulty")
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

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

    // All active users in the lobby (max 4 players) - updates when a user joins or leaves/disconnects from site
    // FetchType.EAGER as only up to 4x users are linked at any one time
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "lobby_users", // Name of the join table
            joinColumns = @JoinColumn(name = "lobby_id"), // Foreign key to the `lobby` entity
            inverseJoinColumns = @JoinColumn(name = "user_id") // Foreign key to the `user` entity
    )
    private Set<UserEntity> userEntities;

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

}
