import { useRef } from "react";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { useJoinPrivateLobby } from "@/hooks/lobby/useJoinPrivateLobby";

export function PrivateLobbyJoinModalPane() {

    // Call hook at top level of component to satisfy hook requirements
    const joinPrivateLobby = useJoinPrivateLobby();

    const inputRef = useRef<HTMLInputElement>(null);

    const handleClick = () => {
        const joinCode = inputRef.current?.value;
        if (joinCode) {
            joinPrivateLobby.mutate(joinCode);
        }
    }

    return (
        <div className="flex flex-col items-center gap-4 mt-12">
            <Input id="join-code-input" className="w-[75%]" ref={inputRef} type="text" placeholder="Enter join code" required />
            <Button id="join-private-btn" className="w-[20%] cursor-pointer" onClick={handleClick}>Join</Button>
        </div>
    )
}