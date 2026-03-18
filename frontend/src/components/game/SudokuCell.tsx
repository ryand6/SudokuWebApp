import type { PlayerColour } from "@/types/enum/PlayerColour";
import { playerColourClassNamePicker } from "@/utils/game/cellUtils";
import React from "react";

const SudokuCell = React.memo(function SudokuCell(
    {
        row, 
        col, 
        value, 
        notes, 
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

    return (
        <div onClick={onSelect}
            className={`w-full h-full flex items-center justify-center 
                        cursor-pointer box-border
                        animate-fill-cell 
                        ${(isInRow || isInCol || isInBlock) && !isSelected ? playerColourClassName.faded : "bg-primary-foreground"}
                        ${isSelected ? playerColourClassName.standard : "bg-primary-foreground"}
                        ${isSameNumber ? "font-extrabold text-2xl" : "font-semibold text-xl"}
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