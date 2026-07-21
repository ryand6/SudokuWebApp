package com.github.ryand6.sudokuweb.mappers;

import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import com.github.ryand6.sudokuweb.mappers.Impl.user.UserEntityDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
Integration tests for UserEntityDtoMapper
*/
public class UserEntityDtoMapperIntegrationTests extends AbstractIntegrationTest {

    private final UserEntityDtoMapper mapper;

    // Autowired handles mapper dependency injection - required for integration tests
    @Autowired
    public UserEntityDtoMapperIntegrationTests(
            UserEntityDtoMapper mapper
    ) {
        this.mapper = mapper;
    }

    @BeforeEach
    public void setUp() {
    }

    @Test
    void mapToDto_shouldReturnValidUserDto() {
        UserEntity userEntity = UserEntity.builder()
                .id(123L)
                .username("testuser")
                .isOnline(true)
                .build();

        UserDto dto = mapper.mapToDto(userEntity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(123L);
        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.isOnline()).isTrue();
    }

//    @Test
//    void mapFromDto_withProviderAndProviderId_shouldReturnValidUserEntity() {
//        UserDto dto = UserDto.builder()
//                .id(2L)
//                .username("mapperuser")
//                .isOnline(true)
//                .score(scoreEntityDtoMapper.mapToDto(savedScore))
//                .build();
//
//        String provider = "dummyProvider";
//        String providerId = "dummyProviderId";
//
//        UserEntity entity = mapper.mapFromDto(dto, provider, providerId);
//
//        assertThat(entity).isNotNull();
//        assertThat(entity.getId()).isEqualTo(2L);
//        assertThat(entity.getUsername()).isEqualTo("mapperuser");
//        assertThat(entity.getProvider()).isEqualTo(provider);
//        assertThat(entity.getProviderId()).isEqualTo(providerId);
//        assertThat(entity.getIsOnline()).isTrue();
//        assertThat(entity.getScoreEntity().getId()).isEqualTo(savedScore.getId());
//    }
//
//    @Test
//    void mapFromDto_withoutProviderId_shouldThrowUnsupportedOperationException() {
//        UserDto dto = UserDto.builder()
//                .id(3L)
//                .username("noPassUser")
//                .isOnline(true)
//                .score(scoreEntityDtoMapper.mapToDto(savedScore))
//                .build();
//
//        assertThatThrownBy(() -> mapper.mapFromDto(dto))
//                .isInstanceOf(UnsupportedOperationException.class)
//                .hasMessageContaining("Provider and Provider ID required");
//    }

}
