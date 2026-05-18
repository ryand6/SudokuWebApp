import { useTimeRemaining } from "@/hooks/global/useTimeRemaining";
import { convertMillisecondsToMinuteClock } from "@/utils/time/convertMillisecondsToMinuteClock";
import React from "react";

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
    const timeRemaining = useTimeRemaining(endTime);

    // Don't show timer once it's ended
    if (timeRemaining <= 0) {
        if (timerEndAction) timerEndAction();
        return null;
    };

    return (
        <span className={`${className}`}>{ convertMillisecondsToMinuteClock(timeRemaining) }</span>
    );
})

export default BasicTimer;