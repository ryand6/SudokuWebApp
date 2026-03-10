package com.github.ryand6.sudokuweb.events.types.lobby;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LobbyPlayerLeftMembershipEvent {

    private Long lobbyId;

    private Long userId;

}
