package com.github.ryand6.sudokuweb.services.impl;

import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.exceptions.UserNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.UsernameTakenException;
import com.github.ryand6.sudokuweb.mappers.Impl.UserEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import com.github.ryand6.sudokuweb.util.OAuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserEntityDtoMapper userEntityDtoMapper;

    public UserService(UserRepository userRepository,
                       UserEntityDtoMapper userEntityDtoMapper) {
        this.userRepository = userRepository;
        this.userEntityDtoMapper = userEntityDtoMapper;
    }

    // Try retrieve User's OAuth provider name and ID
    public UserDto getCurrentUserByOAuth(OAuth2User principal, OAuth2AuthenticationToken authToken) {
        String provider = OAuthUtil.retrieveOAuthProviderName(authToken);
        String providerId = OAuthUtil.retrieveOAuthProviderId(provider, principal);
        Optional<UserEntity> user = userRepository.findByProviderAndProviderId(provider, providerId);
        if (user.isPresent()) {
            return userEntityDtoMapper.mapToDto(user.get());
        } else {
            return null;
        }
    }

    // Create user when they log into site for the first time
    public UserDto createNewUser(String username, String provider, String providerId) {
        //Check if username already exists in DB
        if (userRepository.existsByUsername(username)) {
            throw new UsernameTakenException("Username provided is taken, please choose another");
        }

        // Create score entity first so that it can be persisted when User entity is persisted
        ScoreEntity score = new ScoreEntity();
        score.setTotalScore(0);
        score.setGamesPlayed(0);
        // Persist the user entity to DB
        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setProvider(provider);
        newUser.setProviderId(providerId);
        newUser.setIsOnline(true);
        newUser.setScoreEntity(score);
        return userEntityDtoMapper.mapToDto(userRepository.save(newUser));
    }

    // Get top 5 players based on their total score
    public List<UserDto> getTop5PlayersTotalScore() {
        // Return the top 5
        Pageable topFive = PageRequest.of(0, 5);
        Page<UserEntity> topFivePage = userRepository.findByOrderByScoreEntity_TotalScoreDesc(topFive);
        return topFivePage.getContent()
                .stream()
                .map(userEntityDtoMapper::mapToDto)
                .toList();
    }

    // Gets the players rank based on their total_score when compared to all other players
    public Long getPlayerRank(Long userId) {
        return userRepository.getUserRankById(userId);
    }

    // Get user entity from DB via their id
    public UserEntity findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

}
