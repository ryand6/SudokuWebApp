import { computeSecondsLeftUntilTimestamp } from "@/utils/time/timeDifference";
import { useEffect, useState } from "react";

export function GameCountdownScreen({
    gameStartsAt
}: {
    gameStartsAt: string | null
}) {

    const [countdown, setCountdown] = useState(computeSecondsLeftUntilTimestamp(gameStartsAt));

    console.log(countdown);
    
    useEffect(() => {
        if (countdown > 0) {
            const timer = setTimeout(() => {
                setCountdown(prevCountdown => prevCountdown - 1);
            }, 1000);

            return () => clearTimeout(timer);
        }
    }, [countdown]);

    return (
        <div className="flex flex-col items-center justify-center h-full w-full bg-background">
            {
                countdown > 0 && (
                    <h1 className="animate-game-start-countdown text-primary-foreground text-center">
                        {countdown}
                    </h1>
                )
            }
        </div>
    );
};