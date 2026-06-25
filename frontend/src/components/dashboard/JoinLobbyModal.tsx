import { useState } from "react";
import { PrivateLobbyJoinModalPane } from "./PrivateLobbyJoinModalPane";
import { PublicLobbyList } from "./PublicLobbyList";
import { IconTrophy } from '@tabler/icons-react';
import { IconBeach } from '@tabler/icons-react';
import { IconWorld } from '@tabler/icons-react';
import { IconLock } from '@tabler/icons-react';

export function JoinLobbyModal({
    isMobile
}: {
    isMobile: boolean
}) {
    const [gameType, setGameType] = useState<"ranked" | "casual">("ranked");
    const [pane, setPane] = useState<"public" | "private">("public");

    const togglePublic = () => {
        setPane("public");
    }

    const togglePrivate = () => {
        setPane("private");
    }

    const toggleStatus = (event: React.MouseEvent<HTMLButtonElement>) => {
        const buttonText = event.currentTarget.textContent.toLowerCase();
        if (buttonText === "public" || buttonText === "private") {
            setPane(buttonText);
        }
    }

    const iconSize: number = isMobile ? 16 : 24;
    const iconStroke: number = isMobile ? 2 : 3;

    return (
        <div id="modal-content" className="flex flex-col w-full h-full bg-background">
            <div className="flex items-end justify-between gap-5 w-full px-3 pb-5 pt-8 bg-sidebar">
                <div className="flex flex-col gap-2 items-start justify-end">
                    <div className="font-display font-semibold text-2xl text-primary-foreground tracking-wide">
                        Join Lobby
                    </div>
                    <div className="font-display text-xs text-muted">
                        Browse open lobbies or enter a private code
                    </div>
                </div>
                {
                    pane === "public" && (
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
                    )
                }
                

            </div>
            <div className="flex border-b-1 border-muted">
                <div 
                    onClick={togglePublic} 
                    className={`flex flex-1 cursor-pointer justify-center items-center py-2 font-display gap-1
                                hover:bg-primary/60 hover:text-primary-foreground hover:font-bold 
                                ${pane === "public" ? 'bg-background font-bold border-b-2 border-primary text-primary' : 'bg-muted/70 text-muted-foreground'}`}
                >
                    <IconWorld size={iconSize} />
                    Public
                </div>
                <div 
                    onClick={togglePrivate} 
                    className={`flex flex-1 cursor-pointer justify-center items-center font-display gap-1
                                hover:bg-primary/60 hover:text-primary-foreground hover:font-bold 
                                ${pane === "private" ? 'bg-background font-bold border-b-2 border-primary text-primary' : 'bg-muted/70 text-muted-foreground'}`}
                >
                    <IconLock size={iconSize} />
                    Private
                </div>
            </div>
            <div id="modal-right-pane" className="flex flex-col p-2 overflow-y-auto">
                {pane === "public" ? <PublicLobbyList /> : <PrivateLobbyJoinModalPane isMobile={isMobile} />}
            </div>
        </div>
    )
}