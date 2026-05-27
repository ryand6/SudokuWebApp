import { useDotsAnimation } from "@/hooks/global/useDotsAnimation"

export function WaitingForPlayersScreen() {

    const dotsCount = useDotsAnimation();

    return (
        <div className="relative flex flex-col items-center justify-center h-full w-full">
            <h1 
                className="font-bold animate-pulse text-primary text-center"
            >
                Waiting for other players
                <span className={dotsCount >= 1 ? "opacity-100" : "opacity-0"}>.</span>
                <span className={dotsCount >= 2 ? "opacity-100" : "opacity-0"}>.</span>
                <span className={dotsCount >= 3 ? "opacity-100" : "opacity-0"}>.</span>
            </h1>
        </div>
    )
};