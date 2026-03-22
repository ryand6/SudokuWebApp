package com.github.ryand6.sudokuweb.domain.game.state;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "shared_game_states")
public class SharedGameStateEntity {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "game_id", nullable = false, unique = true)
    private GameEntity gameEntity;

    @ElementCollection
    @CollectionTable(
            name = "cell_first_ownership",
            joinColumns = {
                    @JoinColumn(name = "game_id", referencedColumnName = "game_id", nullable = false)
            }
    )
    @MapKeyColumn(name = "cell_index")
    @Column(name = "user_id")
    private Map<Integer, Long> cellFirstOwnership = new HashMap<>();

    // Track the user ID of the last player to claim a first on a cell
    @Column(name = "last_first_winner_user_id")
    private Long lastFirstWinnerUserId;

    @Column(name = "current_shared_board_state", nullable = true)
    private String currentSharedBoardState;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private Long version;

    //#######################//
    // Domain Business Logic //
    //#######################//

    public FirstClaimEvaluationResult evaluateFirstClaim(int cellIndex, Long userId, boolean hasCellMistakeOccurred) {
        boolean firstWon = !hasCellMistakeOccurred && isFirstWon(cellIndex);
        boolean streakContinued = firstWon && isStreakContinued(userId);
        if (firstWon) {
            addFirstWin(cellIndex, userId);
        }
        return new FirstClaimEvaluationResult(firstWon, streakContinued);
    }

    private void addFirstWin(int cellIndex, Long userId) {
        cellFirstOwnership.put(cellIndex, userId);
    }

    private boolean isFirstWon(int cellIndex) {
        return cellFirstOwnership.get(cellIndex) == null;
    }

    private boolean isStreakContinued(Long userId) {
        boolean streakContinued = lastFirstWinnerUserId != null && lastFirstWinnerUserId.equals(userId);
        lastFirstWinnerUserId = userId;
        return streakContinued;
    }

}
