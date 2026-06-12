import type { PlayerColour } from "@/types/enum/PlayerColour";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";

export function GameChatToast({
    username,
    message,
    playerColour,
}: {
    username: string;
    message: string;
    playerColour: PlayerColour;
}) {
    const colours = playerColourClassNamePicker[playerColour];

    return (
        <div className="flex items-center gap-2 min-w-56 px-2">
            <div className={`w-6.5 h-6.5 rounded-full flex items-center justify-center shrink-0 ${colours.light}`}>
                <div className={`w-2.5 h-2.5 rounded-full ${colours.strong}`} />
            </div>
            <div className="flex flex-col gap-0.5 min-w-0">
                <span className="font-display text-sm font-semibold text-muted-foreground">
                    {username}
                </span>
                <span className="text-xs text-foreground whitespace-nowrap overflow-hidden text-ellipsis">
                    {message}
                </span>
            </div>
        </div>
    );
}