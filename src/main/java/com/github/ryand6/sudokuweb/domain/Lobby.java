package com.github.ryand6.sudokuweb.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lobbies")
public class Lobby {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lobby_id_seq")
    private Long id;

    @Column(name = "lobby_name", nullable = false)
    private String lobbyName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // true if lobby open, false if no players active in the lobby anymore
    @Column(name = "is_active")
    private Boolean isActive;

    // true for public lobby, false for private
    @Column(name = "is_public")
    private Boolean isPublic;

    // All active users in the lobby (max 4 players)
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "lobby_users", // Name of the join table
            joinColumns = @JoinColumn(name = "lobby_id"), // Foreign key to the `lobby` entity
            inverseJoinColumns = @JoinColumn(name = "user_id") // Foreign key to the `user` entity
    )
    private Set<User> users;

    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL)
    private Set<LobbyState> lobbyStates;

}
