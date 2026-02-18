import type { PlayerState } from "@/pages/GamePage";
import SudokuCell from "./SudokuCell";


export function SudokuBoard({playerGameState}: {playerGameState: PlayerState}) {

    return (
        <div className="">
            <div className="grid grid-cols-9 grid-rows-9 w-200 h-200">
                {playerGameState.boardState.map((row, r) =>
                    row.map((cell, c) => {
                        // Tailwind border logic
                        const borderTop = r % 3 === 0 ? "border-t-4 border-black" : "border-t-1 border-black";
                        const borderLeft = c % 3 === 0 ? "border-l-4 border-black" : "border-l-1 border-black";
                        const borderBottom = r === 8 ? "border-b-4 border-black" : "border-b-1 border-black";
                        const borderRight = c === 8 ? "border-r-4 border-black" : "border-r-1 border-black";

                        return (
                            <SudokuCell 
                                key={`${r}-${c}`}
                                index={(r * 8) + c}
                                row={r} 
                                col={c} 
                                value={cell.value}
                                notes={cell.notes}

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