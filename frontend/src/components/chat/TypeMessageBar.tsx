import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import type { Dispatch, SetStateAction } from "react";

export function TypeMessageBar({
    inputMessage,
    setInputMessage,
    handleClick
}: {
    inputMessage: string,
    setInputMessage: Dispatch<SetStateAction<string>>,
    handleClick: () => void
}) {
    return (
        <div className="flex flex-col justify-between gap-3">
            <Textarea 
                id="game-chat-input" 
                placeholder="Type your message..."
                className="text-lg!"
                value={inputMessage} 
                onChange={(e) => {
                    setInputMessage(e.target.value);
                }}
                onKeyDown={(e) => {
                    if (e.key === "Enter" && !e.shiftKey) {
                        e.preventDefault();
                        handleClick();
                    }
                }}
            />
            <div className="flex justify-end">
                <Button 
                    onClick={handleClick}
                    className="cursor-pointer py-5 px-10"
                >
                    <span className="font-display text-lg">Send</span>
                </Button>
            </div>
            
        </div>
    )
}