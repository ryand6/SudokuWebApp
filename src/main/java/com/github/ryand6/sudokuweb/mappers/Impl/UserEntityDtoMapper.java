package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.ScoreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserEntityDtoMapper implements EntityDtoMapper<UserEntity, UserDto> {

    private final ScoreRepository scoreRepository;

    public UserEntityDtoMapper(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }


    @Override
    public UserDto mapToDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .isOnline(userEntity.getIsOnline())
                .scoreId(userEntity.getScoreEntity().getId())
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
        ScoreEntity scoreEntity = resolveDtoScore(userDto.getScoreId());

        UserEntity.UserEntityBuilder userEntityBuilder = UserEntity.builder()
                .username(userDto.getUsername())
                .passwordHash(passwordHash)
                .isOnline(userDto.getIsOnline())
                .scoreEntity(scoreEntity);

        // Don't assign id field if non-existent, DB will create
        if (userDto.getId() != null) {
            userEntityBuilder.id(userDto.getId());
        }

        return userEntityBuilder.build();
    }

    // Get ScoreEntity through DTO ScoreId
    private ScoreEntity resolveDtoScore(Long scoreId) {
        return scoreRepository.findById(scoreId)
                .orElseThrow(() -> new EntityNotFoundException("Score not found with id " + scoreId));
    }

}
