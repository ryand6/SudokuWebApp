import type { GameEventDto } from "@/types/dto/entity/game/GameEventDto";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";
import { getLocalTime } from "@/utils/time/getLocalTime";

export function GameEventMessage({
    msg,
    playerColours
}: {
    msg: GameEventDto,
    playerColours: Record<number, PlayerColour> | undefined
}) {

    console.log(msg);
    console.log(msg.createdAt);
    console.log(getLocalTime(msg.createdAt));

    return (
        <div className="flex flex-row w-full py-2 px-4 border-muted border-b-1 hover:bg-sidebar-primary/20 items-center gap-4">
            <div className="flex w-[20%] items-center gap-2">
                {
                    playerColours && (
                        <div 
                            className={`left-0 p-2 my-1 border-muted border-1 rounded-full ${playerColourClassNamePicker[playerColours[msg.userId]].medium}`}
                        >
                        </div>
                    )
                }
                <span className="font-semibold text-lg overflow-ellipsis text-accent-foreground font-display tracking-wide">{ msg.username }</span>
            </div>
            <div className="flex w-[70%] whitespace-pre-line gap-2 font-sans text-accent-foreground">
                <span>—</span>
                <span className="tracking-wide">{msg.message}</span>
            </div>
            <div className="flex justify-end w-[10%]">
                <span className="font-mono">
                    {getLocalTime(msg.createdAt)}
                </span>
            </div>
        </div>
    );
}