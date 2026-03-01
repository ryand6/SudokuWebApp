package com.github.ryand6.sudokuweb.domain.game;

import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.GameEventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "game_events",
        indexes = {
                @Index(name = "idx_game_id_sequence_number", columnList = "game_id, sequence_number")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"game_id", "sequence_number"})
        }
)
public class GameEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_event_id_seq")
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity gameEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameEventType eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> payload;

    @Column(name = "sequence_number", nullable = false)
    private long sequenceNumber;

}
