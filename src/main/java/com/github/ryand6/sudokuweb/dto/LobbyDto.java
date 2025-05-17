package com.github.ryand6.sudokuweb.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbyDto {

    private Long id;

    private String lobbyName;

    private LocalDateTime createdAt;

    private Boolean isActive;

    private Boolean isPublic;

    private Set<UserDto> users;

}
