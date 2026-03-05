package com.github.ryand6.sudokuweb.dto.entity.user;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String username;

    private boolean isOnline;

}
