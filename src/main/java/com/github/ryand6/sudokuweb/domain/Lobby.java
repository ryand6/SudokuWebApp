package com.github.ryand6.sudokuweb.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lobby {

    private Long id;

    private String lobby_name;

    private LocalDateTime created_at;

    private Boolean is_active;

}
