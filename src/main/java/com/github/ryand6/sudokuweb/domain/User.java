package com.github.ryand6.sudokuweb.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Entity that maps to users db table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Long id;

    private String username;

    private String password_hash;

    private LocalDateTime created_at;

}
