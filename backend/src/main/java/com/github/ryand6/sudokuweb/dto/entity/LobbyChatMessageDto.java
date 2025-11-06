package com.github.ryand6.sudokuweb.dto.entity;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbyChatMessageDto {

    private Long id;

    private Long lobbyId;

    private String username;

    private String message;

    private Instant createdAt;

}
