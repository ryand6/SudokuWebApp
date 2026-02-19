import React from "react";

const SudokuCell = React.memo(function SudokuCell({row, col, value, notes, className}: {row: number, col: number, value: string | undefined, notes: number, className: string}) {
    return (
        <div 
            className={`w-full h-full flex items-center justify-center 
                        text-xl font-semibold cursor-pointer box-border
                        animate-fill-cell bg-primary-foreground
                        ${className}`}
            style={{ animationDelay: `${(row + col) * 400}ms`}}
        >
            <span>
                {value ? value : null}
            </span>
        </div>
    )
})

export default SudokuCell;