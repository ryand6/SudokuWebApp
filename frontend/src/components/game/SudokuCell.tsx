import React from "react";

const SudokuCell = React.memo(function SudokuCell({row, col, index, value, notes, className}: {row: number, col: number, index: number, value: string | undefined, notes: number, className: string}) {
    return (
        <div 
            className={`w-full h-full flex items-center justify-center 
                        text-xl font-semibold cursor-pointer box-border
                        animate-fill-cell text-primary-foreground bg-primary-foreground
                        ${className}`}
            style={{ animationDelay: `${(3 * row + 3 * col) * 100}ms`}}
        >
            <span>
                {value ? value : null}
            </span>
        </div>
    )
})

export default SudokuCell;