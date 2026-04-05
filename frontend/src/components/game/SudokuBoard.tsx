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

    const opponentHighlightedCells: Map<number, CellCoordinates> | undefined = gameHighlightedCells ? new Map(gameHighlightedCells) : undefined;
    opponentHighlightedCells && opponentHighlightedCells.delete(userId);

    const playerColours: Record<number, PlayerColour> = useMemo(() => {
        const newObj: Record<number, PlayerColour> = {};
        Object.keys(gamePlayers).forEach((key) => newObj[Number(key)] = gamePlayers[Number(key)].colour);
        return newObj;
    }, [gamePlayers]);

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
        <div className="m-2 aspect-square max-h-full max-w-full min-h-0" >
            <div className="grid grid-cols-9 grid-rows-9 h-full w-full">
                {boardState.map((row, r) =>
                    row.map((cell, c) => {
                        // Tailwind border logic
                        const borderTop    = r === 0 ? "border-t-6 border-black" : r % 3 === 0 ? "border-t-4 border-black" : "border-t border-black";
                        const borderLeft   = c === 0 ? "border-l-6 border-black" : c % 3 === 0 ? "border-l-4 border-black" : "border-l border-black";
                        const borderBottom = r === 8 ? "border-b-6 border-black" : "border-b border-black";
                        const borderRight  = c === 8 ? "border-r-6 border-black" : "border-r border-black";

                        const cellIndex: number = getCellIndex(r, c);

                        return (
                            <div className={`
                                        ${borderTop} ${borderLeft} ${borderBottom} ${borderRight}
                                    `}>
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
                                    isInRow={r === playerHighlightedCell?.row}
                                    isInCol={c === playerHighlightedCell?.col}
                                    isInBlock={isCellInSameBlock(r, c, playerHighlightedCell)}
                                    isSameNumber={playerHighlightedCell ? boardState[r][c].value === boardState[playerHighlightedCell.row][playerHighlightedCell.col].value : false}
                                    cellOwnership={cellFirstOwnership[cellIndex]}
                                    onSelect={handleCellSelect}
                                    notesModeOn={notesModeOn}
                                    highlightedCellNumber={playerHighlightedCell ? boardState[playerHighlightedCell.row][playerHighlightedCell.col].value : undefined}
                                    opponentsHighlighted={getHighlightedOpponents(r, c)}
                                />
                            </div> 
                        )
                    }
                ))}
            </div>
        </div>

    )
}