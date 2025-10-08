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
        <div>
            <Input id="join-code-input" ref={inputRef} type="text" placeholder="Enter join code" required />
            <Button id="join-private-btn" onClick={handleClick}>Join</Button>
        </div>
    )
}