import { useState } from "react";
import { Button } from "../ui/button";
import { PrivateLobbyJoinModalPane } from "./PrivateLobbyJoinModalPane";
import { PublicLobbyList } from "./PublicLobbyList";

export function JoinLobbyModal() {
    const [pane, setPane] = useState<"public" | "private">("public");

    const toggleStatus = (event: React.MouseEvent<HTMLButtonElement>) => {
        const buttonText = event.currentTarget.textContent.toLowerCase();
        if (buttonText === "public" || buttonText === "private") {
            setPane(buttonText);
        }
    }

    return (
        <div id="modal-content" className="flex w-full h-full relative">
            <div id="modal-left-pane" className="w-1/5 bg-card p-1 flex flex-col">
                <Button onClick={toggleStatus} className={`border-border border-2 flex-1 -mb-1 cursor-pointer rounded-none rounded-t-md hover:bg-primary hover:text-primary-foreground hover:font-bold ${pane === "public" ? 'bg-sidebar-primary font-bold' : 'bg-card text-foreground'}`}>Public</Button>
                <Button onClick={toggleStatus} className={`border-border border-2 flex-1 cursor-pointer rounded-none rounded-b-md hover:bg-primary hover:text-primary-foreground hover:font-bold ${pane === "private" ? 'bg-sidebar-primary font-bold' : 'bg-card text-foreground'}`}>Private</Button>
            </div>
            <div id="modal-right-pane" className="flex flex-col w-4/5 p-2 overflow-y-auto">
                {pane === "public" ? <PublicLobbyList /> : <PrivateLobbyJoinModalPane />}
            </div>
        </div>
    )
}