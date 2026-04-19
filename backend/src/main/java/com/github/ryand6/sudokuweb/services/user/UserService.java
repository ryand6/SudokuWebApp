package com.github.ryand6.sudokuweb.services.user;

import com.github.ryand6.sudokuweb.domain.user.UserFactory;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.domain.user.oauth.UserOAuthProviderEntity;
import com.github.ryand6.sudokuweb.domain.user.oauth.UserOAuthProviderRepository;
import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.events.types.user.ws.UsernameUpdatedWsEvent;
import com.github.ryand6.sudokuweb.exceptions.auth.InvalidOtpException;
import com.github.ryand6.sudokuweb.exceptions.auth.OAuthProviderNotLinkedException;
import com.github.ryand6.sudokuweb.exceptions.auth.RecoveryEmailNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.user.UserNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.user.UsernameTakenException;
import com.github.ryand6.sudokuweb.mappers.Impl.user.UserEntityDtoMapper;
import com.github.ryand6.sudokuweb.domain.user.UserRepository;
import com.github.ryand6.sudokuweb.util.HashUtils;
import com.github.ryand6.sudokuweb.util.OAuthUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserEntityDtoMapper userEntityDtoMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final OtpService otpService;
    private final EmailService emailService;
    private final CacheManager cacheManager;


    public UserService(UserRepository userRepository,
                       UserEntityDtoMapper userEntityDtoMapper,
                       ApplicationEventPublisher applicationEventPublisher,
                       OtpService otpService,
                       EmailService emailService,
                       CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.userEntityDtoMapper = userEntityDtoMapper;
        this.applicationEventPublisher = applicationEventPublisher;
        this.otpService = otpService;
        this.emailService = emailService;
        this.cacheManager = cacheManager;
    }

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Value("${hmac.email.secret-key}")
    private String hmacSecretKey;

    @Cacheable(value = "userCache", key = "#authToken.authorizedClientRegistrationId + '_' + #principal.name")
    // Try retrieve User's OAuth provider name and ID
    public UserDto getCurrentUserByOAuth(OAuth2User principal, OAuth2AuthenticationToken authToken) {
        String provider = OAuthUtil.retrieveOAuthProviderName(authToken);

        log.info("Cache MISS - fetching user from DB for provider: {}", provider);

        log.info("Cache key: {}_{}", authToken.getAuthorizedClientRegistrationId(), principal.getName());

        Cache cache = cacheManager.getCache("userCache");
        CaffeineCache caffeineCache = (CaffeineCache) cache;
        log.info("Cache stats: {}", caffeineCache.getNativeCache().stats());

        String providerId = OAuthUtil.retrieveOAuthProviderId(provider, principal);

        UserEntity user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new OAuthProviderNotLinkedException("No account linked to this OAuth provider"));
        return userEntityDtoMapper.mapToDto(user);
    }

    // Get the userEntity for internal use so that the entity can be updated
    UserEntity getCurrentUserEntityByOAuth(OAuth2User principal, OAuth2AuthenticationToken authToken) {
        String provider = OAuthUtil.retrieveOAuthProviderName(authToken);
        String providerId = OAuthUtil.retrieveOAuthProviderId(provider, principal);
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    // Create user when they log into site for the first time
    public void createNewUser(String username, String provider, String providerId, String recoveryEmail) {
        //Check if username already exists in DB
        if (userRepository.existsByUsername(username)) {
            throw new UsernameTakenException("Username provided is taken, please choose another");
        }
        String recoveryEmailHash = HashUtils.generateHmacHash(recoveryEmail, hmacSecretKey);
        UserEntity newUser = UserFactory.createUser(username, provider, providerId, recoveryEmailHash);

        userRepository.save(newUser);
    }

    // Link a new OAuth provider to existing account - user must provide valid recovery email and verify the OTP sent
    @Transactional
    public void requestAccountLink(String emailAddress, OAuth2User principal, OAuth2AuthenticationToken authToken, HttpSession session) {
        String emailHash = HashUtils.generateHmacHash(emailAddress, hmacSecretKey);
        UserEntity user = userRepository.findByRecoveryEmailHash(emailHash)
                .orElseThrow(() -> new RecoveryEmailNotFoundException("No account found with that recovery email"));
        String provider = OAuthUtil.retrieveOAuthProviderName(authToken);
        String providerId = OAuthUtil.retrieveOAuthProviderId(provider, principal);
        // Store pending OAuth provider linking details in session in order to link provider if OTP is verified
        session.setAttribute("pendingLinkProvider", provider);
        session.setAttribute("pendingLinkProviderId", providerId);
        session.setAttribute("pendingLinkUserId", user.getId());
        String otp = otpService.generateAndStoreOtp(user.getId());
        emailService.sendOtpEmail(emailAddress, otp);
    }

    @Transactional
    public void verifyAccountLink(String otp, HttpSession session) {
        String provider = (String) session.getAttribute("pendingLinkProvider");
        String providerId = (String) session.getAttribute("pendingLinkProviderId");
        Long userId = (Long) session.getAttribute("pendingLinkUserId");
        try {
            if (provider == null || providerId == null || userId == null) {
                throw new InvalidOtpException("Session expired, please restart the account linking process");
            }
            otpService.validateOtp(userId, otp);
            UserEntity user = findUserById(userId);
            UserOAuthProviderEntity newProvider = UserOAuthProviderEntity.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .userEntity(user)
                    .build();
            user.getUserOAuthProviderEntities().add(newProvider);
            userRepository.save(user);
        } finally {
            session.removeAttribute("pendingLinkProvider");
            session.removeAttribute("pendingLinkProviderId");
            session.removeAttribute("pendingLinkUserId");
        }
    }

    @Transactional
    @CacheEvict(value = "userCache", key = "#authToken.authorizedClientRegistrationId + '_' + #principal.name")
    // Update a user's username
    public UserDto updateUsername(String username, OAuth2User principal, OAuth2AuthenticationToken authToken) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameTakenException("Username provided is taken, please choose another");
        }
        UserEntity user = getCurrentUserEntityByOAuth(principal, authToken);
        // update username
        user.setUsername(username);
        UserEntity updatedUser = userRepository.save(user);
        UserDto userDto = userEntityDtoMapper.mapToDto(updatedUser);

        // Get providerId so that updated Dto can be sent to correct user via websockets
        String providerName = OAuthUtil.retrieveOAuthProviderName(authToken);
        String providerId = OAuthUtil.retrieveOAuthProviderId(providerName, principal);

        // Publish WS event after commit
        applicationEventPublisher.publishEvent(
                new UsernameUpdatedWsEvent(userDto, providerId)
        );

        return userDto;
    }

    // Get top 5 players based on their total score
    public List<UserDto> getTop5PlayersTotalScore() {
        // Return the top 5
        Pageable topFive = PageRequest.of(0, 5);
        Page<UserEntity> topFivePage = userRepository.findByOrderByUserStatsEntity_TotalScoreDesc(topFive);
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
