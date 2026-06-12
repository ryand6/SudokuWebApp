import type { PlayerColour } from "@/types/enum/PlayerColour";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";
import type { ChatMessageGroup } from "@/utils/game/infiniteDataUtils";
import { getLocalTime } from "@/utils/time/getLocalTime";

export function OutgoingMessageGroup({
    messageGroup,
    playerColours,
    isLastGroup
}: {
    messageGroup: ChatMessageGroup,
    playerColours: Record<number, PlayerColour> | undefined,
    isLastGroup: boolean
}) {

    const playerColourStrong = playerColours ? playerColourClassNamePicker[playerColours[messageGroup.userId]].strong : "";
    const playerColourMedium = playerColours ? playerColourClassNamePicker[playerColours[messageGroup.userId]].medium : "bg-muted/60";
    
    return (
        <div className="flex w-full h-auto px-4 py-2">
            <div className="flex flex-col w-full items-start gap-1 pr-2">
                {
                    messageGroup.messages.map((message, index) => {
                        const isLastMessage: boolean = isLastGroup && index === messageGroup.messages.length - 1;
                        const groupMeta = message.showTimestamp ? (
                            <div className="flex gap-2 items-baseline">
                                <span className="font-mono text-sm text-muted">{getLocalTime(message.createdAt)}</span>
                                {index === 0 && (
                                    <span className="font-semibold  text-lg text-accent-foreground font-display tracking-wide overflow-ellipsis">
                                        {messageGroup.username}
                                    </span>
                                )}
                            </div>
                        ) : <></>
                        return (
                            <div className="flex flex-col items-end w-full gap-1">
                                {groupMeta}
                                <div 
                                    className={`w-auto max-w-[70%] h-auto bg-muted/60 rounded-bl-xl rounded-br-xl rounded-tr-xs 
                                                rounded-tl-xl px-3 py-2 ${isLastMessage && "animate-new-message"} ${playerColourMedium}`}
                                >
                                    <span className="text-lg">{message.message}</span>
                                </div>
                            </div>
                        )
                    }
                )}
            </div>
            <div className="flex flex-col w-10 justify-start items-center">
                {
                    playerColours && (
                        <div className="relative flex items-center justify-center w-6 h-6">
                            <div className={`absolute inset-0 rounded-full opacity-40 animate-pulse ${playerColourStrong}`} />
                            <div className={`w-2 h-2 rounded-full animate-pulse ${playerColourStrong}`} />
                        </div>
                    )
                }
            </div>
        </div>
    )
}