import { useRef } from "react";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { useJoinPrivateLobby } from "@/api/rest/lobby/mutate/useJoinPrivateLobby";
import { IconLock } from "@tabler/icons-react";

export function PrivateLobbyJoinModalPane({
    isMobile
}: {
    isMobile: boolean
}) {

    const joinPrivateLobby = useJoinPrivateLobby();

    const inputRef = useRef<HTMLInputElement>(null);

    const iconSize: number = isMobile ? 16 : 24;

    const handleClick = () => {
        const joinCode = inputRef.current?.value;
        if (joinCode) {
            joinPrivateLobby.mutate(joinCode);
        }
    }

    return (
        <div className="flex flex-col items-center gap-4 mt-12">
            <div className="border-1 border-secondary bg-secondary/30 rounded-full p-3">
                <IconLock size={iconSize} />
            </div>
            <div className="font-display text-xl text-foreground font-semibold">
                Enter a join code
            </div>
            <div className="flex justify-center font-display text-muted-foreground min-w-[50%] max-w-[75%] text-sm">
                To join a private lobby, paste the code shared with you below or alternatively paste the URL shared with you into the URL bar
            </div>
            <Input id="join-code-input" className="min-w-[50%] max-w-[75%]" ref={inputRef} type="text" placeholder="Enter join code..." required />
            <Button id="join-private-btn" className="w-[20%] cursor-pointer font-display font-semibold text-lg" onClick={handleClick}>Join</Button>
        </div>
    )
}