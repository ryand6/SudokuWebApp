import type { PlayerColour } from "@/types/enum/PlayerColour";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";
import type { ChatMessageGroup } from "@/utils/game/infiniteDataUtils";

export function OutgoingMessageGroup({
    messageGroup,
    playerColours
}: {
    messageGroup: ChatMessageGroup,
    playerColours: Record<number, PlayerColour> | undefined
}) {

    const playerColour = playerColours ? playerColourClassNamePicker[playerColours[messageGroup.userId]] : "";
    const playerColourFaded = playerColour ? playerColour + "/30" : "";

    return (
        <>
        </>
    )
}