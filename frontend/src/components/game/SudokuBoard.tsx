import { useState } from "react";
import SudokuCell from "./SudokuCell";
import type { BoardState, CellCoordinates, GamePlayer } from "@/types/game/GameTypes";


export function SudokuBoard({boardState, playerState}: {boardState: BoardState, playerState: GamePlayer}) {

    const [highlightedCell, setHighlightedCell] = useState<CellCoordinates | null>(
        playerState.currentHighlightedCell
    );

    return (
        <div className="">
            <div className="grid grid-cols-9 grid-rows-9 w-200 h-200">
                {boardState.map((row, r) =>
                    row.map((cell, c) => {
                        // Tailwind border logic
                        const borderTop = r % 3 === 0 ? "border-t-4 border-black" : "border-t-1 border-black";
                        const borderLeft = c % 3 === 0 ? "border-l-4 border-black" : "border-l-1 border-black";
                        const borderBottom = r === 8 ? "border-b-4 border-black" : "border-b-1 border-black";
                        const borderRight = c === 8 ? "border-r-4 border-black" : "border-r-1 border-black";

                        return (
                            <SudokuCell 
                                key={`${r}-${c}`}
                                row={r} 
                                col={c} 
                                value={cell.value}
                                notes={cell.notes}
                                isHighlighted={
                                    r === highlightedCell?.row && c === highlightedCell?.col
                                }
                                onClick={() => setHighlightedCell({ row: r, col: c })}

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