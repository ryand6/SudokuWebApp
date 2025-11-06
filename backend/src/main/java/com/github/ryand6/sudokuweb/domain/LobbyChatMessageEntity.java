package com.github.ryand6.sudokuweb.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
// Using index for fast message lookups per each lobby, useful for when multiple lobbies are active
@Table(
        name = "lobby_chat_messages",
        indexes = {
                @Index(name = "idx_lobby_messages_lobby_created", columnList = "lobbyId, createdAt DESC")
        }
)
public class LobbyChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lobby_id", nullable = false)
    private LobbyEntity lobbyEntity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "message", nullable = false)
    private String message;

}
