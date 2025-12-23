import { convertSecondsToMinuteClock } from "@/utils/time/convertSecondsToMinuteClock";
import { IconClock } from "@tabler/icons-react";

export function TimerCountdown({time}: {time: number}) {
    return (
        <div className="flex flex-col justify-center text-white w-auto p-2 mt-2 mr-5 h-min bg-gray-600 rounded-full ">
            <div>Game starts in:</div>
            <div className="flex flex-row">
                <IconClock />
                <span className="pl-1">{ convertSecondsToMinuteClock(time) }</span>
            </div>
        </div>
    );
}