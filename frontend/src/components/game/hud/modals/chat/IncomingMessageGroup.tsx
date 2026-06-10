import type { PlayerColour } from "@/types/enum/PlayerColour";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";
import type { ChatMessageGroup } from "@/utils/game/infiniteDataUtils";

export function IncomingMessageGroup({
    messageGroup,
    playerColours
}: {
    messageGroup: ChatMessageGroup,
    playerColours: Record<number, PlayerColour> | undefined
}) {

    const playerColour = playerColours ? playerColourClassNamePicker[playerColours[messageGroup.userId]].medium : "";


    console.log("Message Group Incoming: ", messageGroup);

    return (
        <div className="flex w-full h-auto px-4 py-2">
            <div className="flex flex-col w-10 justify-start items-center">
                {
                    playerColours && (
                        <div className="relative flex items-center justify-center w-6 h-6">
                            <div className={`absolute inset-0 rounded-full opacity-40 animate-pulse ${playerColour}`} />
                            <div className={`w-2 h-2 rounded-full animate-pulse ${playerColour}`} />
                        </div>
                    )
                }
            </div>
        </div>
    )
}