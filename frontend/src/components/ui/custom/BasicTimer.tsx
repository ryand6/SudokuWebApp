import { useTimeRemaining } from "@/hooks/global/useTimeRemaining";
import { convertMillisecondsToMinuteClock } from "@/utils/time/convertMillisecondsToMinuteClock";
import React, { useEffect } from "react";

const BasicTimer = React.memo(function BasicTimer({
    endTime,
    className,
    timerEndAction
}: {
    endTime: number,
    className?: string,
    timerEndAction?: () => void
}) {
    // Calculate time remaining on countdown clock
    const timeRemaining = useTimeRemaining(endTime, 50);

     useEffect(() => {
        if (timeRemaining <= 0 && timerEndAction) {
            timerEndAction();
        }
    }, [timeRemaining <= 0]);

    if (timeRemaining <= 0) return null;

    return (
        <span className={`${className}`}>{ convertMillisecondsToMinuteClock(timeRemaining) }</span>
    );
})

export default BasicTimer;