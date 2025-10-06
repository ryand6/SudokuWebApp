import { useRef } from "react";
import { Button } from "../ui/button";
import { Input } from "../ui/input";

export function PrivateLobbyJoinModalPane() {

    const inputRef = useRef<HTMLInputElement>(null);

    const handleClick = () => {
        const joinCode = inputRef.current?.value;
    }

    return (
        <div>
            <Input id="join-code-input" ref={inputRef} type="text" placeholder="Enter join code" />
            <Button id="join-private-btn">Join</Button>
        </div>
    )
}