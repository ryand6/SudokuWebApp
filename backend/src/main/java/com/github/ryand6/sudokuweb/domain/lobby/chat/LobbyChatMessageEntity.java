package com.github.ryand6.sudokuweb.domain.lobby.chat;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.MessageType;
import com.github.ryand6.sudokuweb.exceptions.lobby.chat.MessageProfanityException;
import com.github.ryand6.sudokuweb.exceptions.lobby.chat.MessageTooSoonException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
                @Index(name = "idx_lobby_messages_lobby_created", columnList = "lobby_id, created_at DESC")
        }
)
public class LobbyChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lobby_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private LobbyEntity lobbyEntity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity userEntity;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "message_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    //#######################//
    // Domain Business Logic //
    //#######################//

    // Validate if user can send another message in the lobby chat yet (3 second cool down period)
    public void validateMessageTime(Instant lastMessageTime) {
        if (lastMessageTime == null) {
            return;
        }
        // User must wait 5 seconds before another message can be sent
        Long timeSinceMessage = Instant.now().getEpochSecond() - lastMessageTime.getEpochSecond();
        if (timeSinceMessage < 3) {
            Long remainingSeconds = 3 - timeSinceMessage;
            throw new MessageTooSoonException(
                    "Please wait " + remainingSeconds + " more seconds before sending another message",
                    remainingSeconds
            );
        }
    }

}
