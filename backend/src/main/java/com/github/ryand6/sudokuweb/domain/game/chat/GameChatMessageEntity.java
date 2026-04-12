package com.github.ryand6.sudokuweb.domain.game.chat;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.MessageType;
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
// Using index for fast message lookups per each game, useful for when multiple lobbies are active
@Table(
        name = "game_chat_messages",
        indexes = {
                @Index(name = "idx_game_messages_created", columnList = "game_id, created_at DESC")
        }
)
public class GameChatMessageEntity {

    public static final int PAGE_SIZE = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_message_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GameEntity gameEntity;

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

    // Validate if user can send another message in the game chat (5 second cool down period)
    public void validateMessageTime(Instant lastMessageTime) {
        if (lastMessageTime == null) {
            return;
        }
        // User must wait 3 seconds before another message can be sent
        Long timeSinceMessage = Instant.now().getEpochSecond() - lastMessageTime.getEpochSecond();
        if (timeSinceMessage < 5) {
            Long remainingSeconds = 5 - timeSinceMessage;
            throw new MessageTooSoonException(
                    "Please wait " + remainingSeconds + " more seconds before sending another message",
                    remainingSeconds
            );
        }
    }

}
