package com.github.ryand6.sudokuweb.events.types.user.ws;

import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsernameUpdatedWsEvent implements UserUpdateWsEvent {

    private UserDto userDto;

    private String providerId;

}
