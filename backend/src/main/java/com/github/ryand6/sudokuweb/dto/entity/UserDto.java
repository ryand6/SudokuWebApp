package com.github.ryand6.sudokuweb.dto.entity;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String username;

    private Boolean isOnline;

    private ScoreDto score;

}
