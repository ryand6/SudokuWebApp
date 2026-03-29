package com.github.ryand6.sudokuweb.domain.game.player.state;

import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerId;
import com.github.ryand6.sudokuweb.util.StringUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "game_player_states")
public class GamePlayerStateEntity {

    @EmbeddedId
    private GamePlayerId id; // same composite key as GamePlayerEntity

    @MapsId
    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "game_id", referencedColumnName = "game_id", nullable = false),
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    })
    private GamePlayerEntity gamePlayerEntity;

    @Column(name = "current_board_state")
    private String currentBoardState = null;

    // Each pair of bytes in the array is a bitmask acting as a binary representation of the notes active for that cell
    @Column(name = "notes", nullable = false)
    private byte[] notes = new byte[81 * 2];

    @ElementCollection
    @CollectionTable(
            name = "player_mistaken_cells",
            joinColumns = {
                    @JoinColumn(name = "game_id", referencedColumnName = "game_id", nullable = false),
                    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
            }
    )
    @MapKeyColumn(name = "cell_index")
    @Column(name = "number_of_mistakes")
    private Map<Integer, Integer> mistakenCells = new HashMap<>();

    @Column(name = "consecutive_mistake_count")
    private int consecutiveMistakeCount = 0;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0;

    @Column(name = "active_multiplier", nullable = false)
    private double activeMultiplier = 0;

    @Column(name = "multiplier_ends_at")
    private Instant multiplierEndsAt = null;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePlayerStateEntity gamePlayerStateEntity = (GamePlayerStateEntity) o;
        return id != null && id.equals(gamePlayerStateEntity.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    //#######################//
    // Domain Business Logic //
    //#######################//

    public void updateCurrentBoardState(int cellIndex, char value) {
        if (currentBoardState != null) {
            setCurrentBoardState(StringUtils.replaceCharAtIndex(currentBoardState, cellIndex, value));
        }
    }

    public boolean isBoardStateComplete() {
        return currentBoardState.indexOf('.') == -1;
    }

    public int getCellIndex(int row, int col) {
        return (row * 9) + col;
    }

    public void addCellMistake(int cellIndex) {
        Integer mistakeCount = mistakenCells.get(cellIndex);
        mistakeCount = mistakeCount != null ? mistakeCount + 1 : 1;
        mistakenCells.put(cellIndex, mistakeCount);
    }

    public boolean hasCellMistakeOccurred(int cellIndex) {
        return mistakenCells.get(cellIndex) != null;
    }

    public Integer getNumberOfCellMistakes(int cellIndex) {
        return mistakenCells.get(cellIndex);
    }

    public void incrementCurrentStreak() {
        currentStreak += 1;
    }

    public void resetCurrentStreak() {
        currentStreak = 0;
    }

    public void incrementConsecutiveMistakeCount() {
        consecutiveMistakeCount += 1;
    }

    public void resetConsecutiveMistakeCount() {
        consecutiveMistakeCount = 0;
    }

}
