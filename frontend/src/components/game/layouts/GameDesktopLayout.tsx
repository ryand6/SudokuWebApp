import type { GameLayout } from "@/types/layouts/GameLayout";
import { GameHUD } from "../hud/GameHUD";
import { SudokuBoard } from "../board/SudokuBoard";
import { PlayerStatsSummaryBar } from "../players/PlayerStatsSummaryBar";
import { UserActionBar } from "../actions/UserActionBar";
import { getCellState } from "@/utils/game/boardStateUtils";
import { Modal } from "@/components/ui/custom/Modal";
import { GameResults } from "../results/GameResults";
import { GameInfoBar } from "../info/GameInfoBar";
import { Separator } from "@/components/ui/separator";

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
    gameMode,
    queryClient,
    navigate
}: GameLayout) {
    return (
        <>
            <div className="flex items-center w-full h-full">
                <div className="flex flex-col w-[25%] h-full border-border border-r-4 p-4 bg-card">
                    <GameHUD 
                        userId={currentUser.id}
                        gameId={publicGameState.gameId}
                        lobbyId={publicGameState.lobbyId}
                        gamePlayers={publicGameState.players} 
                        currentStreak={privateGameState.currentStreak} 
                        isMobile={isMobile}
                        leaveGameHandler={leaveGameHandler}
                        playerColours={playerColours}
                        gameMode={gameMode}
                    />
                    <Separator className="bg-muted my-4 py-[1px]" />
                    <PlayerStatsSummaryBar 
                        userId={currentUser.id}
                        gamePlayers={publicGameState.players}
                        currentStreak={privateGameState.currentStreak}
                        isMobile={isMobile}
                    />
                </div>
                <div className="flex flex-col justify-center items-center w-[75%] h-full">
                    
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
        </>
    )
}