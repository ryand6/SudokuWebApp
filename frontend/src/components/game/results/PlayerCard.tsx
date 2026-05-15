import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import type { GamePlayer } from "@/types/game/GameTypes";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";

export function PlayerCard({
    player
}: {
    player: GamePlayer
}) {
    const highlight = player.gameResult === "WIN" || player.gameResult === "DRAW";
    const playerColour = playerColourClassNamePicker[player.colour].medium;
 
    return (
        <div
            className={`relative flex flex-col rounded-lg p-3 border-2 ${
                highlight ? "border-result-win bg-accent" : "border-muted bg-card"
            }`}
        >
            {player.gameResult === "WIN" && (
                <span
                    className="absolute -top-2.5 left-1/2 -translate-x-1/2 bg-result-win text-card-foreground text-xs font-semibold 
                                tracking-wider px-2.5 py-0.5 rounded-full whitespace-nowrap capitalize font-display"
                >
                    winner
                </span>
            )}
            {player.gameResult === "DRAW" && (
                <span
                    className="absolute -top-2.5 left-1/2 -translate-x-1/2 bg-input text-card-foreground text-xs font-semibold 
                                tracking-wider px-2.5 py-0.5 rounded-full whitespace-nowrap capitalize font-display"
                >
                    draw
                </span>
            )}
 
            <div className="flex items-center gap-1.5 mb-1.5">
                <span
                    className={`w-2.5 h-2.5 rounded-full ${playerColour}`}
                />
                <span
                    className="text-md font-bold text-foreground font-sans"
                >
                    {player.name}
                </span>
            </div>
 
            {player.gameResult === "FORFEIT" ? (
                <span
                    className="text-md text-muted-foreground font-sans pt-2"
                >
                    ❌ Forfeited
                </span>
            ) : player.finishedGame ? (
                <>
                    <span
                        className={`text-5xl font-semibold leading-none font-display ${
                            highlight ? "text-primary" : "text-secondary"
                        }`}
                    >
                        {player.score?.toLocaleString() ?? "—"}
                    </span>
                    <span
                        className="text-xs text-muted-foreground mt-0.5 font-sans"
                    >
                        points
                    </span>
                </>
            ) : (
                <div className="flex justify-start pt-2">
                    <SpinnerButton text="Still playing…" />
                </div>
                
            )}
        </div>
    );
}
