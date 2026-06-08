import { useEffect, type RefObject } from "react";

export function useResizeBoard(
    containerRef: RefObject<HTMLDivElement | null>,
    gridRef: RefObject<HTMLDivElement | null>, 
    gridMaxPadding: number
) {
    useEffect(() => {
        const observer = new ResizeObserver(([entry]) => {
            const { width, height } = entry.contentRect;
            const size = Math.min(width, height) - gridMaxPadding;
            if (gridRef.current) {
                gridRef.current.style.width = `${size}px`;
                gridRef.current.style.height = `${size}px`;
            }
        });
        if (containerRef.current) {
            observer.observe(containerRef.current);
        } 
        return () => observer.disconnect();
    }, []);
}