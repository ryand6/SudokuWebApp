import { Modal } from "@/components/ui/custom/Modal";
import { UserActionBar } from "../actions/UserActionBar";
import { SudokuBoard } from "../board/SudokuBoard";
import { GameHUD } from "../hud/GameHUD";
import { GameNotificationLayer } from "../notifications/GameNotificationLayer";
import { PlayerStatsSummaryBar } from "../players/PlayerStatsSummaryBar";
import { GameResults } from "../results/GameResults";
import { getCellState } from "@/utils/game/boardStateUtils";
import type { GameLayout } from "@/types/layouts/GameLayout";
import { GameInfoBar } from "../info/GameInfoBar";

export function GameMobileLayout({
    currentUser,
    publicGameState,
    privateGameState,
    boardState,
    playerColours,
    userHighlightedCell,
    gameHighlightedCells,
    setGameHighlightedCells,
    notesModeOn,
    setNotesModeOn,
    showGameResultsModal,
    leaveGameHandler,
    isMobile,
    gameMode,
    queryClient,
    navigate
}: GameLayout) {
    return (
        <div className="flex flex-col h-full w-full">
            <div className="flex justify-center items-center h-full w-full">
                <div className="flex flex-col items-center w-full h-full">
                    <GameHUD 
                        userId={currentUser.id}
                        gameId={publicGameState.gameId}
                        gamePlayers={publicGameState.players} 
                        currentStreak={privateGameState.currentStreak}
                        isMobile={isMobile}
                        leaveGameHandler={leaveGameHandler}
                        playerColours={playerColours}
                        gameMode={gameMode}
                    />
                    <GameInfoBar
                        difficulty={publicGameState.gameSettings.difficulty} 
                        gameMode={publicGameState.gameSettings.gameMode}
                        gameType={publicGameState.gameSettings.gameType}
                        gameEndsAt={publicGameState.gameEndsAt}
                        scoreNotificationsEnabled={currentUser.userSettings.scoreNotificationsEnabled}
                        streakNotificationsEnabled={currentUser.userSettings.streakNotificationsEnabled}
                    />
                    <SudokuBoard 
                        gameId={publicGameState.gameId}
                        userId={currentUser.id}
                        boardState={boardState} 
                        playerColours={playerColours!}
                        gamePlayers={publicGameState.players}
                        cellFirstOwnership={publicGameState.sharedGameState.cellFirstOwnership}
                        gameHighlightedCells={gameHighlightedCells}
                        setGameHighlightedCells={setGameHighlightedCells}
                        notesModeOn={notesModeOn}
                        userSettings={currentUser.userSettings}
                        isMobile={isMobile}
                    />
                    <PlayerStatsSummaryBar 
                        userId={currentUser.id}
                        gamePlayers={publicGameState.players}
                        currentStreak={privateGameState.currentStreak}
                        isMobile={isMobile}
                    />
                    <UserActionBar 
                        gameId={publicGameState.gameId}
                        userId={currentUser.id}
                        initialBoardState={publicGameState.initialBoardState}
                        playerHighlightedCell={userHighlightedCell}
                        highlightedCellState={userHighlightedCell ? getCellState(privateGameState.boardState, userHighlightedCell.row, userHighlightedCell.col) : undefined}
                        isMobile={isMobile}
                        notesModeOn={notesModeOn}
                        setNotesModeOn={setNotesModeOn}
                        queryClient={queryClient}
                    />
                </div>
                <Modal
                    isOpen={showGameResultsModal}
                    className="w-[90%]! h-[90%]! left-[5%]! top-[5%]! md:w-[70%]! md:left-[15%]! lg:w-[50%]! lg:left-[25%]! z-50"
                >
                    <GameResults
                        userId={currentUser.id}
                        gameId={publicGameState.gameId}
                        lobbyId={publicGameState.lobbyId}
                        difficulty={publicGameState.gameSettings.difficulty}
                        gameMode={publicGameState.gameSettings.gameMode}
                        leaderboardResult={privateGameState.leaderboardResult}
                        players={publicGameState.players}
                        gameStartsAt={publicGameState.gameStartsAt}
                        endedPrematurely={publicGameState.endedPrematurely}
                        gameEndedAt={publicGameState.gameEndedAt}
                        queryClient={queryClient}
                        navigate={navigate}
                    />
                </Modal>
            </div>
        </div>
                        
    )
}