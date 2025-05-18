package com.github.ryand6.sudokuweb.dto;

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

    private Boolean isActive;

    private Boolean isPublic;

    private Set<UserDto> users;

}
