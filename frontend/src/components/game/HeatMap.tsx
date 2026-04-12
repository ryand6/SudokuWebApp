import type { CellStatus } from "@/types/enum/CellStatus";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import { getCellIndex } from "@/utils/game/cellUtils";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";
import { useCallback } from "react";

export function HeatMap({
    playerColour,
    boardProgress
}: {
    playerColour: PlayerColour,
    boardProgress: CellStatus[]
}) {
    const board = [...Array(9)].map(() => Array(9).fill(0));

    console.log(board);

    const getCellColour = useCallback((cellIndex: number) => {
        if (boardProgress[cellIndex] === "GIVEN") {
            return 'bg-muted-foreground';
        } 
        return playerColourClassNamePicker[playerColour].medium;
    }, [playerColour, boardProgress]);

    return (
        <div className="mx-2 my-4 aspect-square w-[120px] h-[120px] sm:w-[160px] sm:h-[160px] md:w-[200px] md:h-[200px] lg:w-[300px] lg:h-[300px] min-h-0" >
            <div className="grid grid-cols-9 grid-rows-9 h-full w-full">
                {board.map((row, r) => 
                    row.map((cell, c) => {

                        const borderTop = r === 0 ? "border-t-3 border-grid-line" : r % 3 === 0 ? "border-t-2 border-grid-line" : "";
                        const borderLeft = c === 0 ? "border-l-3 border-grid-line" : c % 3 === 0 ? "border-l-2 border-grid-line" : "";
                        const borderBottom = r === 8 ? "border-b-3 border-grid-line" : "border-b border-grid-line";
                        const borderRight = c === 8 ? "border-r-3 border-grid-line" : "border-r border-grid-line";

                        const cellIndex: number = getCellIndex(r, c);

                        const cellColour = boardProgress[cellIndex] === "INCOMPLETE" ? 'bg-card' : getCellColour(cellIndex);

                        return (
                            <div
                                className={`${borderTop} ${borderLeft} ${borderBottom} ${borderRight} ${cellColour}`} 
                            >
                            </div>
                        )
                    }
                ))}
            </div>
        </div>
    )
}