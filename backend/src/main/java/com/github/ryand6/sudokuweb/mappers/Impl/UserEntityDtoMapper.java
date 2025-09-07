package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserEntityDtoMapper implements EntityDtoMapper<UserEntity, UserDto> {

    private final ScoreEntityDtoMapper scoreEntityDtoMapper;

    public UserEntityDtoMapper(ScoreEntityDtoMapper scoreEntityDtoMapper) {
        this.scoreEntityDtoMapper = scoreEntityDtoMapper;
    }


    @Override
    public UserDto mapToDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .isOnline(userEntity.getIsOnline())
                .score(scoreEntityDtoMapper.mapToDto(userEntity.getScoreEntity()))
                .build();
    }

//    // Additional context required to create the UserEntity (provider and providerId)
//    @Override
//    public UserEntity mapFromDto(UserDto dto) {
//        // Basic mapping without providerId, or throw UnsupportedOperationException
//        throw new UnsupportedOperationException("Provider and Provider ID required, please provide these as parameters when calling mapFromDto on UserDto");
//    }
//
//    // Overloaded method to handle additional context in order to create UserEntity
//    public UserEntity mapFromDto(UserDto userDto, String provider, String providerId) {
//        UserEntity.UserEntityBuilder userEntityBuilder = UserEntity.builder()
//                .username(userDto.getUsername())
//                .provider(provider)
//                .providerId(providerId)
//                .isOnline(userDto.getIsOnline())
//                .scoreEntity(scoreEntityDtoMapper.mapFromDto(userDto.getScore()));
//
//        // Don't assign id field if non-existent, DB will create
//        if (userDto.getId() != null) {
//            userEntityBuilder.id(userDto.getId());
//        }
//
//        return userEntityBuilder.build();
//    }

}
