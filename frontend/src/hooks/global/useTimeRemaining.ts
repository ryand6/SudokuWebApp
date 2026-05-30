import { useEffect, useState } from "react";

export function useTimeRemaining(endTime: number, intervalMs: number) {
     const [timeRemaining, setTimeRemaining] = useState<number>(() => endTime - performance.now());

    useEffect(() => {
        // Update every 50ms
        const intervalId = setInterval(() => {
            const remaining = Math.max(0, endTime - Date.now());
            setTimeRemaining(remaining);
            // Remove interval once countdown has ended
            if (remaining <= 0) {
                clearInterval(intervalId);
            }
        }, intervalMs);

        return () => clearInterval(intervalId);
    }, [endTime]);

    return timeRemaining;
}