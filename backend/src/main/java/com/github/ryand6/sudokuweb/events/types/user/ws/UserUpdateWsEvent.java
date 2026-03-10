package com.github.ryand6.sudokuweb.events.types.user.ws;

import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;

public interface UserUpdateWsEvent {

    public UserDto getUserDto();

    public String getProviderId();

}
