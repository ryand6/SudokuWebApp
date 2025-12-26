import { useTimeRemaining } from "@/hooks/global/useTimeRemaining";
import { convertMillisecondsToMinuteClock } from "@/utils/time/convertMillisecondsToMinuteClock";
import { IconClock } from "@tabler/icons-react";

export function TimerCountdown({endTime}: {endTime: number}) {
    // Calculate time remaining on countdown clock
    const timeRemaining = useTimeRemaining(endTime);

    // Don't show timer once it's ended
    if (timeRemaining <= 0) return null;

    return (
        <div className="flex flex-col justify-center text-white w-auto p-2 mt-2 mr-5 h-min bg-gray-600 rounded-full ">
            <div>Game starts in:</div>
            <div className="flex flex-row">
                <IconClock />
                <span className="pl-1">{ convertMillisecondsToMinuteClock(timeRemaining) }</span>
            </div>
        </div>
    );
}