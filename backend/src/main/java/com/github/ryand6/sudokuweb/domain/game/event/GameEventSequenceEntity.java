package com.github.ryand6.sudokuweb.domain.game.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "game_event_sequence")
public class GameEventSequenceEntity {

    @Id
    @Column(name = "game_id")
    private Long gameId;

    @Column(name = "next_val", nullable = false)
    private long nextVal = 1L;

    //#######################//
    // Domain Business Logic //
    //#######################//

    public long getCurrentSequenceNumberAndIncrement() {
        long current = nextVal;
        nextVal += 1;
        return current;
    }

}
