package com.github.ryand6.sudokuweb.dto;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/*
LobbyDto used for transferring necessary Lobby entity fields to frontend
*/
public class LobbyDto {

    private Long id;

    private String lobbyName;

    private Difficulty difficulty;

    private Boolean isActive;

    private Boolean isPublic;

    private Boolean inGame;

    private Set<LobbyPlayerDto> lobbyPlayers;

    private Long hostId;

}
