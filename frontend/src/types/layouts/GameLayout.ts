import type { Dispatch, SetStateAction } from "react";
import type { UserDto } from "../dto/entity/user/UserDto";
import type { PlayerColour } from "../enum/PlayerColour";
import type { CellCoordinates, PrivateBoardState, PrivateGamePlayerState, PublicGameState } from "../game/GameTypes";
import type { QueryClient, UseMutateFunction } from "@tanstack/react-query";
import type { GameDto } from "../dto/entity/game/GameDto";
import type { LeaveGameRequestDto } from "../dto/request/LeaveGameRequestDto";
import type { NavigateFunction } from "react-router-dom";
import type { GameMode } from "../enum/GameMode";

export type GameLayout = {
    currentUser: UserDto,
    publicGameState: PublicGameState,
    privateGameState: PrivateGamePlayerState,
    boardState: PrivateBoardState,
    playerColours: Record<number, PlayerColour> | undefined,
    userHighlightedCell: CellCoordinates | undefined,
    gameHighlightedCells: Map<number, CellCoordinates> | undefined,
    setGameHighlightedCells: Dispatch<SetStateAction<Map<number, CellCoordinates> | undefined>>,
    notesModeOn: boolean,
    setNotesModeOn: Dispatch<SetStateAction<boolean>>,
    showGameResultsModal: boolean,
    leaveGameHandler: {
        mutate: UseMutateFunction<GameDto | null, Error, LeaveGameRequestDto, unknown>;
        isLeaving: boolean;
    },
    isMobile: boolean,
    gameMode: GameMode,
    queryClient: QueryClient,
    navigate: NavigateFunction
}