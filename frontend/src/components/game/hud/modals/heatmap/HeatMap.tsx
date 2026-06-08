import { useResizeBoard } from "@/hooks/global/useResizeBoard";
import type { CellStatus } from "@/types/enum/CellStatus";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import { getCellIndex } from "@/utils/game/cellUtils";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";
import { useCallback, useRef } from "react";

export function HeatMap({
    playerColour,
    boardProgress,
    isMobile
}: {
    playerColour: PlayerColour,
    boardProgress: CellStatus[],
    isMobile: boolean
}) {
    const board = [...Array(9)].map(() => Array(9).fill(0));

    const getCellColour = useCallback((cellIndex: number) => {
        if (boardProgress[cellIndex] === "GIVEN") {
            return 'bg-muted-foreground';
        } 
        return playerColourClassNamePicker[playerColour].medium;
    }, [playerColour, boardProgress]);

    const containerRef = useRef<HTMLDivElement>(null);
    const gridRef = useRef<HTMLDivElement>(null);

    const gridMaxPadding = isMobile ? 20 : 30;

    useResizeBoard(containerRef, gridRef, gridMaxPadding);

    return (
        <div 
           ref={containerRef}
            className="w-full h-full flex items-center justify-center" 
        >
            <div ref={gridRef} className="grid grid-cols-9 grid-rows-9 h-full w-full">
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
                                key={`${r}-${c}`}
                            >
                            </div>
                        )
                    }
                ))}
            </div>
        </div>
    )
}