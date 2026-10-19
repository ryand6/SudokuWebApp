package com.github.ryand6.sudokuweb.domain.lobby.settings;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LobbyDifficultyUpdate {

    private Long lobbyId;

    private Difficulty difficulty;

}
