package com.github.ryand6.sudokuweb.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// Entity that maps to lobbyPlayers db table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lobbyPlayers")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    // Name of OAuth2 provider User is registered using
    @Column(name = "provider", nullable = false)
    private String provider;

    // Unique OAuth2 provider ID used to authenticate
    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_online")
    private Boolean isOnline;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "score_id", nullable = false, unique = true)
    private ScoreEntity scoreEntity;

}
