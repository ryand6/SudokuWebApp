import type { PlayerColour } from "@/types/enum/PlayerColour";
import { onHoverHandler, onLeaveHandler, playerColourClassNamePicker } from "@/utils/game/cellUtils";
import React, { useState } from "react";

const SudokuCell = React.memo(function SudokuCell(
    {
        row, 
        col, 
        value, 
        notes,
        isRejected, 
        playerColour,
        isSelected,
        isInRow,
        isInCol,
        isInBlock,
        isSameNumber,
        selectedOpponentsColours,
        onSelect,
        className
    }: {
        row: number, 
        col: number, 
        value: string | undefined, 
        notes: number,
        isRejected: boolean, 
        playerColour: PlayerColour,
        isSelected: boolean, 
        isInRow: boolean,
        isInCol: boolean,
        isInBlock: boolean,
        isSameNumber: boolean,
        selectedOpponentsColours: PlayerColour[] | undefined
        onSelect: () => void,
        className: string
    }
) {
    const playerColourClassName = playerColourClassNamePicker[playerColour];

    const [isHovered, setIsHovered] = useState(false);

    return (
        <div 
            onClick={onSelect}
            onMouseEnter={onHoverHandler(setIsHovered)}
            onMouseLeave={onLeaveHandler(setIsHovered)}
            className={`w-full h-full flex items-center justify-center 
                        cursor-pointer box-border
                        animate-fill-cell 
                        ${(isInRow || isInCol || isInBlock) && !isSelected && !isHovered ? playerColourClassName.light : "bg-primary-foreground"}
                        ${(isHovered && !isSelected) && playerColourClassName.medium}
                        ${isSelected ? playerColourClassName.strong : "bg-primary-foreground"}
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