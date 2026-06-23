import { useState } from "react";
import { Button } from "../ui/button";
import { PrivateLobbyJoinModalPane } from "./PrivateLobbyJoinModalPane";
import { PublicLobbyList } from "./PublicLobbyList";
import { IconTrophy } from '@tabler/icons-react';
import { IconBeach } from '@tabler/icons-react';

export function JoinLobbyModal({
    isMobile
}: {
    isMobile: boolean
}) {
    const [gameType, setGameType] = useState<"ranked" | "casual">("ranked");
    const [pane, setPane] = useState<"public" | "private">("public");

    const toggleStatus = (event: React.MouseEvent<HTMLButtonElement>) => {
        const buttonText = event.currentTarget.textContent.toLowerCase();
        if (buttonText === "public" || buttonText === "private") {
            setPane(buttonText);
        }
    }

    const iconSize: number = isMobile ? 16 : 24;
    const iconStroke: number = isMobile ? 2 : 3;

    return (
        <div id="modal-content" className="flex flex-col w-full h-full">
            <div className="flex items-center justify-between gap-5 w-full px-3 pb-5 pt-8 bg-sidebar">
                <div className="flex flex-col gap-2 items-start justify-end">
                    <div className="font-display font-semibold text-xl text-primary-foreground tracking-wide">
                        Join Lobby
                    </div>
                    <div className="font-display text-xs text-muted">
                        Browse open lobbies or enter a private code
                    </div>
                </div>
                <div className="flex p-1 rounded-full border-1 border-muted">
                    <div 
                        className={`px-3 py-1 font-display rounded-full cursor-pointer text-muted flex items-center gap-2
                                    ${gameType === "casual" && "bg-secondary! text-secondary-foreground! font-semibold!"}`}
                        onClick={() => setGameType("casual")}
                    >
                        <IconBeach size={iconSize} />
                        <span>Casual</span>
                    </div>
                    <div 
                        className={`px-3 py-1 font-display rounded-full cursor-pointer text-muted flex items-center gap-2
                                    ${gameType === "ranked" && "bg-sidebar-primary! text-sidebar-primary-foreground! font-semibold!"}`}
                        onClick={() => setGameType("ranked")} 
                    >
                        <IconTrophy size={iconSize} />
                        <span>Ranked</span>
                    </div>
                </div>

            </div>
            <div id="modal-left-pane" className="bg-card p-1 flex flex-col">
                <Button 
                    onClick={toggleStatus} 
                    className={`border-border border-2 flex-1 cursor-pointer rounded-none rounded-t-md 
                                hover:bg-primary/80 hover:text-primary-foreground hover:font-bold 
                                ${pane === "public" ? 'bg-primary font-bold' : 'bg-primary/50 text-foreground'}`}
                >
                    Public
                </Button>
                <Button 
                    onClick={toggleStatus} 
                    className={`border-border border-2 border-t-0 flex-1 cursor-pointer rounded-none rounded-b-md 
                                hover:bg-primary/80 hover:text-primary-foreground hover:font-bold 
                                ${pane === "private" ? 'bg-primary font-bold' : 'bg-primary/50 text-foreground'}`}
                >
                    Private
                </Button>
            </div>
            <div id="modal-right-pane" className="flex flex-col p-2 overflow-y-auto">
                {pane === "public" ? <PublicLobbyList /> : <PrivateLobbyJoinModalPane />}
            </div>
        </div>
    )
}