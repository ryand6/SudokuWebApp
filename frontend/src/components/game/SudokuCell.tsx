import type { PlayerColour } from "@/types/enum/PlayerColour";
import { getPlayerCorner, onHoverHandler, onLeaveHandler } from "@/utils/game/cellUtils";
import React, { useMemo, useState } from "react";
import NotesLayer from "./NotesLayer";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";

const SudokuCell = React.memo(function SudokuCell(
    {
        row, 
        col, 
        value, 
        notes,
        userId,
        isRejected, 
        playerColours,
        isSelected,
        isInRow,
        isInCol,
        isInBlock,
        isSameNumber,
        cellOwnership,
        onSelect,
        notesModeOn,
        highlightedCellNumber,
        opponentsHighlighted
    }: {
        row: number, 
        col: number, 
        value: string | undefined, 
        notes: number,
        userId: number,
        isRejected: boolean, 
        playerColours: Record<number, PlayerColour>,
        isSelected: boolean, 
        isInRow: boolean,
        isInCol: boolean,
        isInBlock: boolean,
        isSameNumber: boolean,
        cellOwnership: number | undefined,
        onSelect: (r: number, c: number) => void,
        notesModeOn: boolean,
        highlightedCellNumber: string | undefined,
        opponentsHighlighted: number[]
    }
) {
    const playerColourClassName = playerColourClassNamePicker[playerColours[userId]];
    const [isHovered, setIsHovered] = useState(false);
    const isInUnit: boolean = (isInRow || isInCol || isInBlock);

    const playerCornerIndex: Record<number, number> = useMemo(() => {
        return Object.keys(playerColours)
            .reduce((acc, playerId, index) => {
                acc[Number(playerId)] = index;
                return acc;
            }, {} as Record<number, number>)
    }, [playerColours]);

    const showValue: boolean = (value && !isRejected) || (value && isRejected && notes === 0) ? true : false;

    return (
        <div className="relative h-full w-full overflow-hidden">
            <NotesLayer 
                key={`notes${row}-${col}`}
                row={row} 
                col={col} 
                value={value}
                userId={userId}
                notes={notes}
                isRejected={isRejected}
                notesModeOn={notesModeOn}
                highlightedCellNumber={highlightedCellNumber}
                playerColour={playerColours[userId]}
            />
            {opponentsHighlighted && opponentsHighlighted.map((opponentId, index) => {
                const cornerPosition = getPlayerCorner(playerCornerIndex[opponentId]);
                const opponentColourClassName = playerColourClassNamePicker[playerColours[opponentId]].strong;
                return (
                    <div 
                        className={`absolute rotate-45 z-0 h-4 w-4
                                    ${cornerPosition}
                                    ${opponentColourClassName}`}
                        key={`corner-${index}`}
                    >
                    </div>
                )
            })}
            <div 
                onClick={() => {onSelect(row, col)}}
                onMouseEnter={onHoverHandler(setIsHovered)}
                onMouseLeave={onLeaveHandler(setIsHovered)}
                className={`w-full h-full flex items-center justify-center 
                            cursor-pointer box-border
                            animate-fill-cell 
                            ${cellOwnership && !isSelected && !isHovered && !isInUnit ? playerColourClassNamePicker[playerColours[cellOwnership]].medium : "bg-primary-foreground"}
                            ${(isInUnit && !isSelected && !isHovered) && playerColourClassName.light}
                            ${(isHovered && !isSelected) && playerColourClassName.medium}
                            ${isSelected && playerColourClassName.strong}
                            ${isSameNumber ? "font-bold md:font-extrabold text-2xl md:text-3xl" : "font-semibold text-xl md:text-2xl"}
                            ${isRejected ? "text-red-500 text-2xl" : "text-black"}
                            `}
                style={{ animationDelay: `${((row * 3) + col) * 15}ms`}}
            >
                <span className="font-mono">
                    {showValue ? value : null}
                </span>
            </div>
        </div>
    )
})

export default SudokuCell;