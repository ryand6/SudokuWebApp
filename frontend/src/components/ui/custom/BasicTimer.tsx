import { useTimeRemaining } from "@/hooks/global/useTimeRemaining";
import type { TimerUnit } from "@/types/enum/TimerUnit";
import { convertMillisecondsToMinuteClock } from "@/utils/time/convertMillisecondsToMinuteClock";
import React, { useEffect } from "react";

const BasicTimer = React.memo(function BasicTimer({
    endTime,
    className,
    timerEndAction,
    unit
}: {
    endTime: number,
    className?: string,
    timerEndAction?: () => void,
    unit: TimerUnit
}) {
    // Calculate time remaining on countdown clock
    const timeRemaining = useTimeRemaining(endTime, 500);

    useEffect(() => {
        if (timeRemaining <= 0 && timerEndAction) {
            timerEndAction();
        }
    }, [timeRemaining <= 0]);

    if (timeRemaining <= 0) return null;

    const convertedTime = unit === "MINUTES" ? convertMillisecondsToMinuteClock(timeRemaining) 
        : Math.max(0, Math.ceil(timeRemaining / 1000));

    return (
        <span className={`${className}`}>{ convertedTime }</span>
    );
})

export default BasicTimer;