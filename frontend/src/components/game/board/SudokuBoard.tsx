import { useCallback, useEffect, useRef, type Dispatch, type SetStateAction } from "react";
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
        userSettings,
        isMobile
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
        userSettings: UserSettingsDto,
        isMobile: boolean
    }
) {
    const { send } = useWebSocketContext();

    const containerRef = useRef<HTMLDivElement>(null);
    const gridRef = useRef<HTMLDivElement>(null);

    const gridMaxPadding = isMobile ? 20 : 30;

    useEffect(() => {
        const observer = new ResizeObserver(([entry]) => {
            const { width, height } = entry.contentRect;
            const size = Math.min(width, height) - gridMaxPadding;
            if (gridRef.current) {
                gridRef.current.style.width = `${size}px`;
                gridRef.current.style.height = `${size}px`;
            }
        });
        if (containerRef.current) {
            observer.observe(containerRef.current);
        } 
        return () => observer.disconnect();
    }, []);

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
        <div ref={containerRef} className="w-full h-full flex items-center justify-center">
            <div ref={gridRef} className="grid grid-cols-9 grid-rows-9 h-full w-full">
                {boardState.map((row, r) =>
                    row.map((cell, c) => {
                        let borderTop, borderLeft, borderBottom, borderRight;
                        // Tailwind border logic
                        if (isMobile) {
                            borderTop = r === 0 ? "border-t-3 border-black" : r % 3 === 0 ? "border-t-3 border-black" : "border-t border-black";
                            borderLeft = c === 0 ? "border-l-3 border-black" : c % 3 === 0 ? "border-l-3 border-black" : "border-l border-black";
                            borderBottom = r === 8 ? "border-b-3 border-black" : "";
                            borderRight = c === 8 ? "border-r-3 border-black" : "";
                        } else {
                            borderTop = r === 0 ? "border-t-6 border-black" : r % 3 === 0 ? "border-t-6 border-black" : "border-t border-black";
                            borderLeft = c === 0 ? "border-l-6 border-black" : c % 3 === 0 ? "border-l-6 border-black" : "border-l border-black";
                            borderBottom = r === 8 ? "border-b-6 border-black" : "border-b border-black";
                            borderRight = c === 8 ? "border-r-6 border-black" : "border-r border-black";
                        }
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