import type { GameLayout } from "@/types/layouts/GameLayout";
import { GameNotificationLayer } from "../notifications/GameNotificationLayer";
import { GameHUD } from "../hud/GameHUD";
import { SudokuBoard } from "../board/SudokuBoard";
import { PlayerStatsSummaryBar } from "../players/PlayerStatsSummaryBar";
import { UserActionBar } from "../actions/UserActionBar";
import { getCellState } from "@/utils/game/boardStateUtils";
import { Modal } from "@/components/ui/custom/Modal";
import { GameResults } from "../results/GameResults";
import { GameInfoBar } from "../info/GameInfoBar";

export function GameDesktopLayout({
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
    queryClient,
    navigate
}: GameLayout) {
    return (
        <>
            <GameNotificationLayer 
                scoreNotificationsEnabled={currentUser.userSettings.scoreNotificationsEnabled}
                streakNotificationsEnabled={currentUser.userSettings.streakNotificationsEnabled}
            />
            <div className="flex justify-center items-center min-h-[500px] h-full py-[2%]">
                <div className="flex flex-col justify-center items-center w-[80%] max-w-[1200px] h-full">
                    <GameHUD 
                        userId={currentUser.id}
                        gameId={publicGameState.gameId}
                        gamePlayers={publicGameState.players} 
                        currentStreak={privateGameState.currentStreak} 
                        isMobile={isMobile}
                        leaveGameHandler={leaveGameHandler}
                        queryClient={queryClient}
                    />
                    <GameInfoBar
                        difficulty={publicGameState.gameSettings.difficulty} 
                        gameMode={publicGameState.gameSettings.gameMode}
                        gameType={publicGameState.gameSettings.gameType}
                        gameEndsAt={publicGameState.gameEndsAt}
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
                    />
                    <PlayerStatsSummaryBar 
                        userId={currentUser.id}
                        gamePlayers={publicGameState.players}
                        currentStreak={privateGameState.currentStreak}
                    />
                    <UserActionBar 
                        gameId={publicGameState.gameId}
                        userId={currentUser.id}
                        initialBoardState={publicGameState.initialBoardState}
                        playerHighlightedCell={userHighlightedCell}
                        highlightedCellState={userHighlightedCell ? getCellState(privateGameState.boardState, userHighlightedCell.row, userHighlightedCell.col) : undefined}
                        notesModeOn={notesModeOn}
                        setNotesModeOn={setNotesModeOn}
                        playerColours={playerColours!}
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
        </>
    )
}