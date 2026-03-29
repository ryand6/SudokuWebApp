import type { PlayerColour } from "@/types/enum/PlayerColour";
import { onHoverHandler, onLeaveHandler, playerColourClassNamePicker } from "@/utils/game/cellUtils";
import React, { useState } from "react";

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
        className: string
    }
) {
    const playerColourClassName = playerColourClassNamePicker[playerColours[userId]];

    const baseBackground = cellOwnership !== undefined ? playerColourClassNamePicker[playerColours[cellOwnership]] : "bg-primary-foreground";

    const [isHovered, setIsHovered] = useState(false);

    return (
        <div 
            onClick={() => {onSelect(row, col)}}
            onMouseEnter={onHoverHandler(setIsHovered)}
            onMouseLeave={onLeaveHandler(setIsHovered)}
            className={`w-full h-full flex items-center justify-center 
                        cursor-pointer box-border
                        animate-fill-cell 
                        ${(isInRow || isInCol || isInBlock) && !isSelected && !isHovered ? playerColourClassName.light : baseBackground}
                        ${(isHovered && !isSelected) && playerColourClassName.medium}
                        ${isSelected ? playerColourClassName.strong : baseBackground}
                        ${isSameNumber ? "font-extrabold text-2xl" : "font-semibold text-xl"}
                        ${isRejected ? "text-red-500 text-2xl" : "text-black"}
                        ${className}`}
            style={{ animationDelay: `${((row * 3) + col) * 15}ms`}}
        >
            <span>
                {value ? value : null}
            </span>
        </div>
    )
})

export default SudokuCell;