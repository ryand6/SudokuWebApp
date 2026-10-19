package com.github.ryand6.sudokuweb.domain.lobby.player;

import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyPlayerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LobbyPlayerJoinedUpdate {

    private Long lobbyId;

    private LobbyPlayerDto lobbyPlayer;

}
