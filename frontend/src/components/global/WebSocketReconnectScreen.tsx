import { useWebSocketContext } from "@/context/WebSocketProvider";
import { useDotsAnimation } from "@/hooks/global/useDotsAnimation";
import BasicTimer from "../ui/custom/BasicTimer";

export function WebSocketReconnectScreen() {
    const dotsCount = useDotsAnimation();
    const { nextReconnectAt } = useWebSocketContext();

    // console.log(nextReconnectAt);

    return (
        <div className="relative flex flex-col gap-2 text-primary items-center justify-center h-full w-full">
            <h1 
                className="font-bold text-center"
            >
                Reconnecting
                <span className={dotsCount >= 1 ? "opacity-100" : "opacity-0"}>.</span>
                <span className={dotsCount >= 2 ? "opacity-100" : "opacity-0"}>.</span>
                <span className={dotsCount >= 3 ? "opacity-100" : "opacity-0"}>.</span>
            </h1>
            <div>
                <span>Restoring connection to the server</span>
            </div>
            {
                nextReconnectAt && (
                    <div>
                        Next attempt in <BasicTimer endTime={nextReconnectAt} unit={"SECONDS"} /> seconds
                    </div>
                )
            }
        </div>
    )
}