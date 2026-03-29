import { useCallback, useMemo, type Dispatch, type SetStateAction } from "react";
import SudokuCell from "./SudokuCell";
import type { BoardState, CellCoordinates, GamePlayers } from "@/types/game/GameTypes";
import { isCellInSameBlock } from "@/utils/game/blockUtils";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { updateGameHighlightedCellsAndSendWsUpdate } from "@/utils/game/cellUtils";


export function SudokuBoard(
    {
        gameId,
        userId,
        boardState, 
        gamePlayers, 
        gameHighlightedCells,
        setGameHighlightedCells
    }: {
        gameId: number,
        userId: number,
        boardState: BoardState, 
        gamePlayers: GamePlayers,
        gameHighlightedCells: Map<number, CellCoordinates> | undefined,
        setGameHighlightedCells: Dispatch<SetStateAction<Map<number, CellCoordinates> | undefined>>
    }
) {
    const { send } = useWebSocketContext();

    const playerHighlightedCell: CellCoordinates | undefined = gameHighlightedCells ? gameHighlightedCells.get(userId) : undefined;

    return (
        <div className="m-2">
            <div className="grid grid-cols-9 grid-rows-9 w-200 h-200">
                {boardState.map((row, r) =>
                    row.map((cell, c) => {
                        // Tailwind border logic
                        const borderTop = r % 3 === 0 ? "border-t-4 border-black" : "border-t-1 border-black";
                        const borderLeft = c % 3 === 0 ? "border-l-4 border-black" : "border-l-1 border-black";
                        const borderBottom = r === 8 ? "border-b-4 border-black" : "border-b-1 border-black";
                        const borderRight = c === 8 ? "border-r-4 border-black" : "border-r-1 border-black";

                        const selectedOpponentsColors = useMemo(() => {
                            if (!gameHighlightedCells) return undefined;
                            return Array.from(gameHighlightedCells.entries())
                                        .filter(([id, coords]) => id !== userId && coords.row === r && coords.col === c)
                                        .map(([id]) => gamePlayers[id].colour)
                        }, [gameHighlightedCells, userId, r, c, gamePlayers]);

                        const handleCellSelect = useCallback(() => {
                            setGameHighlightedCells(prev => updateGameHighlightedCellsAndSendWsUpdate(send, gameId, userId, prev, { row: r, col: c }))
                        }, [send, gameId, userId, r, c]);

                        return (
                            <SudokuCell 
                                key={`${r}-${c}`}
                                row={r} 
                                col={c} 
                                value={cell.value}
                                notes={cell.notes}
                                isRejected={cell.isRejected}
                                playerColour={gamePlayers[userId].colour}
                                isSelected={
                                    r === playerHighlightedCell?.row && c === playerHighlightedCell?.col
                                }
                                isInRow = {r === playerHighlightedCell?.row}
                                isInCol = {c === playerHighlightedCell?.col}
                                // Implement
                                isInBlock = {isCellInSameBlock(r, c, playerHighlightedCell)}
                                isSameNumber = {playerHighlightedCell ? boardState[r][c].value === boardState[playerHighlightedCell.row][playerHighlightedCell.col].value : false}
                                selectedOpponentsColours={selectedOpponentsColors}
                                // Replace - create a function that updates the state object
                                onSelect={handleCellSelect}

                                className={`
                                    ${borderTop} ${borderLeft} ${borderBottom} ${borderRight}
                                `}
                            />
                        )
                    }
                ))}
            </div>
        </div>

    )
}