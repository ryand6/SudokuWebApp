import { useCallback, useMemo, type Dispatch, type SetStateAction } from "react";
import SudokuCell from "./SudokuCell";
import type { PrivateBoardState, CellCoordinates, GamePlayers } from "@/types/game/GameTypes";
import { isCellInSameBlock } from "@/utils/game/blockUtils";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { getCellIndex, updateGameHighlightedCellsAndSendWsUpdate } from "@/utils/game/cellUtils";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import type { UserSettingsDto } from "@/types/dto/entity/user/UserSettingsDto";


export function SudokuBoard(
    {
        gameId,
        userId,
        boardState, 
        playerColours,
        cellFirstOwnership,
        gameHighlightedCells,
        setGameHighlightedCells,
        notesModeOn,
        userSettings
    }: {
        gameId: number,
        userId: number,
        boardState: PrivateBoardState, 
        playerColours: Record<number, PlayerColour>,
        gamePlayers: GamePlayers,
        cellFirstOwnership: Record<number, number>,
        gameHighlightedCells: Map<number, CellCoordinates> | undefined,
        setGameHighlightedCells: Dispatch<SetStateAction<Map<number, CellCoordinates> | undefined>>,
        notesModeOn: boolean,
        userSettings: UserSettingsDto
    }
) {
    const { send } = useWebSocketContext();

    const playerHighlightedCell: CellCoordinates | undefined = gameHighlightedCells ? gameHighlightedCells.get(userId) : undefined;

    const opponentHighlightedCells: Map<number, CellCoordinates> | undefined = gameHighlightedCells ? new Map(gameHighlightedCells) : undefined;
    opponentHighlightedCells && opponentHighlightedCells.delete(userId);

    const handleCellSelect = useCallback((r: number, c: number) => {
        setGameHighlightedCells(prev => updateGameHighlightedCellsAndSendWsUpdate(send, gameId, userId, prev, { row: r, col: c }))
    }, [send, gameId, userId]);

    const getHighlightedOpponents = useCallback((r: number, c: number) => {
        const opponentsHighlighted: number[] = [];
        gameHighlightedCells?.forEach((value, key) => {
            if (key !== userId && (value.row === r && value.col === c)) {
                opponentsHighlighted.push(key);
            }
        });
        return opponentsHighlighted;
    }, [gameId, userId, gameHighlightedCells]);

    return (
        <div className="mx-2 my-4 aspect-square w-[300px] h-[300px] sm:w-[400px] sm:h-[400px] md:w-[600px] md:h-[600px] lg:w-[800px] lg:h-[800px] min-h-0" >
            <div className="grid grid-cols-9 grid-rows-9 h-full w-full">
                {boardState.map((row, r) =>
                    row.map((cell, c) => {
                        // Tailwind border logic
                        const borderTop = r === 0 ? "border-t-6 border-black" : r % 3 === 0 ? "border-t-4 border-black" : "border-t border-black";
                        const borderLeft = c === 0 ? "border-l-6 border-black" : c % 3 === 0 ? "border-l-4 border-black" : "border-l border-black";
                        const borderBottom = r === 8 ? "border-b-6 border-black" : "border-b border-black";
                        const borderRight = c === 8 ? "border-r-6 border-black" : "border-r border-black";

                        const cellIndex: number = getCellIndex(r, c);

                        return (
                            <div 
                                className={`${borderTop} ${borderLeft} ${borderBottom} ${borderRight}`}
                                key={`container${r}-${c}`}    
                            >
                                <SudokuCell 
                                    key={`cell${r}-${c}`}
                                    row={r} 
                                    col={c} 
                                    value={cell.value}
                                    userId={userId}
                                    notes={cell.notes}
                                    isRejected={cell.isRejected}
                                    playerColours={playerColours}
                                    isSelected={r === playerHighlightedCell?.row && c === playerHighlightedCell?.col}
                                    isInRow={userSettings.highlightedHousesEnabled && r === playerHighlightedCell?.row}
                                    isInCol={userSettings.highlightedHousesEnabled && c === playerHighlightedCell?.col}
                                    isInBlock={userSettings.highlightedHousesEnabled && isCellInSameBlock(r, c, playerHighlightedCell)}
                                    isSameNumber={playerHighlightedCell ? boardState[r][c].value === boardState[playerHighlightedCell.row][playerHighlightedCell.col].value : false}
                                    cellOwnership={userSettings.highlightedFirstsEnabled ? cellFirstOwnership[cellIndex] : undefined}
                                    onSelect={handleCellSelect}
                                    notesModeOn={notesModeOn}
                                    highlightedCellNumber={playerHighlightedCell ? boardState[playerHighlightedCell.row][playerHighlightedCell.col].value : undefined}
                                    opponentsHighlighted={userSettings.opponentHighlightedSquaresEnabled ? getHighlightedOpponents(r, c) : []}
                                />
                            </div> 
                        )
                    }
                ))}
            </div>
        </div>

    )
}