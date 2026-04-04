import { useCallback, useMemo, type Dispatch, type SetStateAction } from "react";
import SudokuCell from "./SudokuCell";
import type { PrivateBoardState, CellCoordinates, GamePlayers } from "@/types/game/GameTypes";
import { isCellInSameBlock } from "@/utils/game/blockUtils";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { getCellIndex, updateGameHighlightedCellsAndSendWsUpdate } from "@/utils/game/cellUtils";
import type { PlayerColour } from "@/types/enum/PlayerColour";


export function SudokuBoard(
    {
        gameId,
        userId,
        boardState, 
        gamePlayers,
        cellFirstOwnership,
        gameHighlightedCells,
        setGameHighlightedCells,
        notesModeOn
    }: {
        gameId: number,
        userId: number,
        boardState: PrivateBoardState, 
        gamePlayers: GamePlayers,
        cellFirstOwnership: Record<number, number>,
        gameHighlightedCells: Map<number, CellCoordinates> | undefined,
        setGameHighlightedCells: Dispatch<SetStateAction<Map<number, CellCoordinates> | undefined>>,
        notesModeOn: boolean
    }
) {
    const { send } = useWebSocketContext();

    const playerHighlightedCell: CellCoordinates | undefined = gameHighlightedCells ? gameHighlightedCells.get(userId) : undefined;

    const playerColours: Record<number, PlayerColour> = useMemo(() => {
        const newObj: Record<number, PlayerColour> = {};
        Object.keys(gamePlayers).forEach((key) => newObj[Number(key)] = gamePlayers[Number(key)].colour);
        return newObj;
    }, [gamePlayers]);

    const handleCellSelect = useCallback((r: number, c: number) => {
        setGameHighlightedCells(prev => updateGameHighlightedCellsAndSendWsUpdate(send, gameId, userId, prev, { row: r, col: c }))
    }, [send, gameId, userId]);

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

                        const cellIndex: number = getCellIndex(r, c);

                        return (
                            <SudokuCell 
                                key={`${r}-${c}`}
                                row={r} 
                                col={c} 
                                value={cell.value}
                                userId={userId}
                                notes={cell.notes}
                                isRejected={cell.isRejected}
                                playerColours={playerColours}
                                isSelected={r === playerHighlightedCell?.row && c === playerHighlightedCell?.col}
                                isInRow={r === playerHighlightedCell?.row}
                                isInCol={c === playerHighlightedCell?.col}
                                isInBlock={isCellInSameBlock(r, c, playerHighlightedCell)}
                                isSameNumber={playerHighlightedCell ? boardState[r][c].value === boardState[playerHighlightedCell.row][playerHighlightedCell.col].value : false}
                                cellOwnership={cellFirstOwnership[cellIndex]}
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