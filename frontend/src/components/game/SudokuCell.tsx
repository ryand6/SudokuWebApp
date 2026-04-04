import type { PlayerColour } from "@/types/enum/PlayerColour";
import { onHoverHandler, onLeaveHandler, playerColourClassNamePicker } from "@/utils/game/cellUtils";
import React, { useState } from "react";
import NotesLayer from "./NotesLayer";

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
        className
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
        className: string
    }
) {
    const playerColourClassName = playerColourClassNamePicker[playerColours[userId]];
    const [isHovered, setIsHovered] = useState(false);
    const isInUnit: boolean = (isInRow || isInCol || isInBlock);

    return (
        <div className="relative h-full w-full">
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
                            ${isSameNumber ? "font-extrabold text-2xl" : "font-semibold text-xl"}
                            ${isRejected ? "text-red-500 text-2xl" : "text-black"}
                            ${className}`}
                style={{ animationDelay: `${((row * 3) + col) * 15}ms`}}
            >
                <span>
                    {value ? value : null}
                </span>
            </div>
        </div>
    )
})

export default SudokuCell;