package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.dto.entity.ScoreDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class ScoreEntityDtoMapper implements EntityDtoMapper<ScoreEntity, ScoreDto> {

    @Override
    public ScoreDto mapToDto(ScoreEntity scoreEntity) {
        return ScoreDto.builder()
                .id(scoreEntity.getId())
                .totalScore(scoreEntity.getTotalScore())
                .gamesPlayed(scoreEntity.getGamesPlayed())
                .build();
    }

//    public ScoreEntity mapFromDto(ScoreDto scoreDto) {
//        ScoreEntity.ScoreEntityBuilder scoreEntityBuilder = ScoreEntity.builder()
//                .totalScore(scoreDto.getTotalScore())
//                .gamesPlayed(scoreDto.getGamesPlayed());
//
//        // Don't assign id field if non-existent, DB will create
//        if (scoreDto.getId() != null) {
//            scoreEntityBuilder.id(scoreDto.getId());
//        }
//
//        return scoreEntityBuilder.build();
//    }

}
