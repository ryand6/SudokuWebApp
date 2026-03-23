package com.github.ryand6.sudokuweb.domain.game.state;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.*;

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

    // Used to provide cell highlighting in frontend representative of which players claimed which cells first
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

    // Used to track the claim position of each user when they claim a cell to determine scoring in some game modes
    @ElementCollection
    @CollectionTable(
            name = "cell_claim_counts",
            joinColumns = {
                    @JoinColumn(name = "game_id", referencedColumnName = "game_id", nullable = false)
            }
    )
    @MapKeyColumn(name = "cell_index")
    @Column(name = "claim_count")
    private Map<Integer, Integer> cellClaimCounts = new HashMap<>();

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

    public CellClaimEvaluationResult evaluateCellClaim(int cellIndex, Long userId, boolean hasCellMistakeOccurred) {
        boolean firstWon = !hasCellMistakeOccurred && isFirstWon(cellIndex);
        Long previousFirstWinner = lastFirstWinnerUserId;
        int claimPosition;
        if (firstWon) {
            updateLastFirstWinnerId(userId);
            addFirstWin(cellIndex);
            addFirstOwnership(cellIndex, userId);
            claimPosition = 1;
        } else if (!hasCellMistakeOccurred) {
            claimPosition = incrementCellClaim(cellIndex);
        } else {
            // User made a mistake on cell at some point
            claimPosition = -1;
        }
        return new CellClaimEvaluationResult(claimPosition, previousFirstWinner);
    }

    private void addFirstOwnership(int cellIndex, Long userId) {
        cellFirstOwnership.put(cellIndex, userId);
    }

    private void addFirstWin(int cellIndex) {
        cellClaimCounts.put(cellIndex, 1);
    }

    private int incrementCellClaim(int cellIndex) {
        int claimCount = cellClaimCounts.getOrDefault(cellIndex, 0);
        claimCount += 1;
        cellClaimCounts.put(cellIndex, claimCount);
        return claimCount;
    }

    private boolean isFirstWon(int cellIndex) {
        return cellClaimCounts.get(cellIndex) == null;
    }

    private void updateLastFirstWinnerId(Long userId) {
        lastFirstWinnerUserId = userId;
    }

}
