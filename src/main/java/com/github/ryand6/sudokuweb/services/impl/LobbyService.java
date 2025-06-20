package com.github.ryand6.sudokuweb.services.impl;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.LobbyDto;
import com.github.ryand6.sudokuweb.exceptions.InvalidLobbyPublicStatusParametersException;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LobbyService {

    private final LobbyRepository lobbyRepository;
    private final UserService userService;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;

    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 25;

    public LobbyService(LobbyRepository lobbyRepository,
                        UserService userService,
                        LobbyEntityDtoMapper lobbyEntityDtoMapper) {
        this.lobbyRepository = lobbyRepository;
        this.userService = userService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
    }

    // Returns a unique join code not used in other private lobbies
    public String generateUniqueCode() {
        String code;
        do {
            code = new Random().ints(CODE_LENGTH, 0, CODE_CHARS.length())
                    .mapToObj(i -> String.valueOf(CODE_CHARS.charAt(i)))
                    .collect(Collectors.joining());
        } while (lobbyRepository.existsByJoinCode(code));
        return code;
    }

    public LobbyDto createNewLobby(String lobbyName, Boolean isPublic, Boolean isPrivate, String joinCode, Long requesterId) {
        LobbyEntity newLobby = new LobbyEntity();
        // One of these must be true or there is an error with the parameters
        if (Boolean.TRUE.equals(isPublic)) {
            newLobby.setIsPublic(true);
        } else if (Boolean.TRUE.equals(isPrivate)) {
            newLobby.setIsPublic(false);
        } else {
            throw new InvalidLobbyPublicStatusParametersException("Either Public or Private lobby must be checked");
        }
        UserEntity requester = userService.findUserById(requesterId);
        newLobby.setLobbyName(lobbyName);
        // requester of lobby creation becomes the host
        newLobby.setHost(requester);
        // Create a set of users only containing the requester for now, until other users join the lobby
        Set<UserEntity> lobbyUsers = new HashSet<>();
        lobbyUsers.add(requester);
        newLobby.setUserEntities(lobbyUsers);
        newLobby.setInGame(false);
        newLobby.setIsActive(true);
        if (joinCode != null && !joinCode.isEmpty()) {
            newLobby.setJoinCode(joinCode);
        }
        return lobbyEntityDtoMapper.mapToDto(lobbyRepository.save(newLobby));
    }

    // Retrieves a page of lobbies based on the specified page number and size. Page results are ordered by createdAt (newest first)
    public List<LobbyDto> getPublicLobbies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return lobbyRepository.findByIsPublicTrueAndIsActiveTrue(pageable)
                .stream()
                .map(lobbyEntityDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

}
