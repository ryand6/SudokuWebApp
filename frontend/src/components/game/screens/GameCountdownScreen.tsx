import { computeSecondsLeftUntilTimestamp } from "@/utils/time/timeDifference";
import { useEffect, useState } from "react";

export function GameCountdownScreen({
    gameStartsAt
}: {
    gameStartsAt: string | null
}) {
    const [countdown, setCountdown] = useState(computeSecondsLeftUntilTimestamp(gameStartsAt));
    
    useEffect(() => {
        let timerId: NodeJS.Timeout;

        if (countdown > 0) {
            timerId = setTimeout(() => {
                setCountdown(prevCountdown => prevCountdown - 1);
            }, 1000);
        }

        return () => {
            if (timerId) {
                clearTimeout(timerId);
            }
        };
    }, [countdown]);

    return (
        <div className="flex flex-col items-center justify-center h-full w-full bg-background">
            {
                countdown > 0 && (
                    <h1 key={countdown} className="animate-game-start-countdown text-9xl font-extrabold text-primary text-center">
                        {countdown}
                    </h1>
                )
            }
        </div>
    );
};