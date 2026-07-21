package com.github.ryand6.sudokuweb.services.leaderboards;

import com.github.ryand6.sudokuweb.domain.leaderboards.LeaderboardsEntity;
import com.github.ryand6.sudokuweb.domain.leaderboards.LeaderboardsRepository;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.domain.user.UserRepository;
import com.github.ryand6.sudokuweb.dto.entity.leaderboards.LeaderboardsDto;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.exceptions.user.UserNotFoundException;
import com.github.ryand6.sudokuweb.mappers.Impl.leaderboards.LeaderboardsEntityDtoMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaderboardsService {

    private final LeaderboardsRepository leaderboardsRepository;
    private final LeaderboardsEntityDtoMapper leaderboardsEntityDtoMapper;
    private final UserRepository userRepository;

    public LeaderboardsService(LeaderboardsRepository leaderboardsRepository,
                               LeaderboardsEntityDtoMapper leaderboardsEntityDtoMapper,
                               UserRepository userRepository) {
        this.leaderboardsRepository = leaderboardsRepository;
        this.leaderboardsEntityDtoMapper = leaderboardsEntityDtoMapper;
        this.userRepository = userRepository;
    }

    public List<LeaderboardsDto> getLeaderboardsResults(GameMode gameMode, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "totalScore"));
        return leaderboardsRepository.findByGameModeOrderByTotalScoreDesc(gameMode, pageable)
                .stream()
                .map(leaderboardsEntityDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public void recordWin(GameMode gameMode, Long userId, Integer leaderboardScore) {
        Optional<LeaderboardsEntity> leaderboardsEntityOptional = leaderboardsRepository.findByGameModeAndUserEntity_Id(gameMode, userId);
        LeaderboardsEntity leaderboardsEntity = leaderboardsEntityOptional.orElseGet(() -> createNewLeaderboardRecord(gameMode, userId));
        leaderboardsEntity.recordWin();
        leaderboardsEntity.applyScore(leaderboardScore);
        leaderboardsRepository.save(leaderboardsEntity);
    }

    public void recordLoss(GameMode gameMode, Long userId, Integer leaderboardScore) {
        Optional<LeaderboardsEntity> leaderboardsEntityOptional = leaderboardsRepository.findByGameModeAndUserEntity_Id(gameMode, userId);
        LeaderboardsEntity leaderboardsEntity = leaderboardsEntityOptional.orElseGet(() -> createNewLeaderboardRecord(gameMode, userId));
        leaderboardsEntity.recordLoss();
        leaderboardsEntity.applyScore(leaderboardScore);
        leaderboardsRepository.save(leaderboardsEntity);
    }

    public void recordDraw(GameMode gameMode, Long userId, Integer leaderboardScore) {
        Optional<LeaderboardsEntity> leaderboardsEntityOptional = leaderboardsRepository.findByGameModeAndUserEntity_Id(gameMode, userId);
        LeaderboardsEntity leaderboardsEntity = leaderboardsEntityOptional.orElseGet(() -> createNewLeaderboardRecord(gameMode, userId));
        leaderboardsEntity.recordDraw();
        leaderboardsEntity.applyScore(leaderboardScore);
        leaderboardsRepository.save(leaderboardsEntity);
    }

    private LeaderboardsEntity createNewLeaderboardRecord(GameMode gameMode, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));
        LeaderboardsEntity leaderboardsEntity = new LeaderboardsEntity();
        leaderboardsEntity.setUserEntity(user);
        leaderboardsEntity.setGameMode(gameMode);
        return leaderboardsEntity;
    }

}
