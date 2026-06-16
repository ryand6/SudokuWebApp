import { Separator } from "@/components/ui/separator";
import type { ChatMessageGroup } from "@/utils/game/infiniteDataUtils";
import { getLocalTime } from "@/utils/time/getLocalTime";

export function InfoMessageGroup({
    messageGroup
}: {
    messageGroup: ChatMessageGroup
}) {
    return (
        <>
            {
                messageGroup.messages.map((message, index) => (
                    <div key={index} className="flex py-2 px-8 justify-stretch items-center gap-2">
                        <Separator orientation="horizontal" className="bg-muted flex-1" />
                        <span className="text-sm text-muted">{messageGroup.username} {message.message}</span>
                        <span className="font-mono text-sm text-muted">{getLocalTime(message.createdAt)}</span>
                        <Separator orientation="horizontal" className="bg-muted flex-1" />
                    </div>
                ))
            }
        </>
        
    )
}