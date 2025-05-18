package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserEntityDtoMapper implements EntityDtoMapper<UserEntity, UserDto> {

    @Override
    public UserDto mapToDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .isOnline(userEntity.getIsOnline())
                .totalScore(userEntity.getScoreEntity().getTotalScore())
                .build();
    }

    // Additional context required to create the UserEntity (passwordHash)
    @Override
    public UserEntity mapFromDto(UserDto dto) {
        // Basic mapping without passwordHash, or throw UnsupportedOperationException
        throw new UnsupportedOperationException("Password hash required, provide passwordHash when calling mapFromDto on UserDto");
    }

    // Overloaded method to handle additional context in order to create UserEntity
    public UserEntity mapFromDto(UserDto userDto, String passwordHash) {
        UserEntity.UserEntityBuilder userEntityBuilder = UserEntity.builder()
                .username(userDto.getUsername())
                .passwordHash(passwordHash)
                .createdAt(LocalDateTime.now())
                .isOnline(userDto.getIsOnline())
                .scoreEntity(ScoreEntity.builder()
                        .totalScore(userDto.getTotalScore() != null ? userDto.getTotalScore() : 0)
                        .gamesPlayed(0)
                        .build());

        // Don't assign id field if non-existent, DB will create
        if (userDto.getId() != null) {
            userEntityBuilder.id(userDto.getId());
        }

        return userEntityBuilder.build();
    }
}
