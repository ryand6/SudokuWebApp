import { IconDoorEnter, IconPlus, IconWorld } from "@tabler/icons-react"
import type { Dispatch, SetStateAction } from "react";
import { Link, type NavigateFunction } from "react-router-dom";

export function OnlinePlayWidget({
    isMobile,
    isActiveLobby,
    setModalOpen,
    navigate
}: {
    isMobile: boolean,
    isActiveLobby: boolean,
    setModalOpen: Dispatch<SetStateAction<boolean>>,
    navigate: NavigateFunction
}) {
    const iconSize: number = isMobile ? 16 : 24;

    return (
        <div className="flex flex-col border-2 border-muted rounded-lg w-full font-display">
            <div className="flex w-full border-b-2 border-b-muted bg-card gap-2 px-4 py-2 items-center text-accent-foreground rounded-t-lg">
                <span><IconWorld size={iconSize} /></span>
                <span className="font-semibold text-lg">Online Play</span>
            </div>
            <div className="flex flex-col w-full h-full p-5 items-center gap-4">
                { isActiveLobby && (
                        <div className="w-full text-center text-destructive">
                            Unable to create or join other lobbies whilst you are in an existing one.
                        </div>
                    )
                }
                <button 
                    className={`flex items-center border-1 w-full rounded-lg px-5 py-3 gap-4
                                ${isActiveLobby ? "bg-muted border-muted opacity-20" : "bg-primary/80 border-primary text-primary-foreground hover:bg-primary/90 cursor-pointer"}`}
                    onClick={() => navigate("/create-lobby")}
                    disabled={isActiveLobby}
                >
                    <div className={`flex justify-center items-center rounded-lg p-2 ${isActiveLobby ? "bg-muted" : "bg-primary"}`}>
                        <IconPlus size={iconSize} />
                    </div>
                    <div className="flex flex-col items-start">
                        <div className="font-semibold text-lg">
                            Create Lobby
                        </div>
                        <div className="text-start">
                            Start a new public or private lobby
                        </div>
                    </div>
                </button>
                 <button 
                    className={`flex items-center border-1 w-full rounded-lg px-5 py-3 gap-4
                                ${isActiveLobby ? "bg-muted border-muted opacity-20" : "bg-secondary/80 border-secondary text-secondary-foreground hover:bg-secondary/90 cursor-pointer"}`}
                    onClick={() => setModalOpen(true)}
                    disabled={isActiveLobby}
                >
                    <div className={`flex justify-center items-center rounded-lg p-2 ${isActiveLobby ? "bg-muted" : "bg-secondary"}`}>
                        <IconDoorEnter size={iconSize} />
                    </div>
                    <div className="flex flex-col items-start">
                        <div className="font-semibold text-lg">
                            Join Lobby
                        </div>
                        <div className="text-start">
                            Browse public lobbies or enter a code to join private lobby
                        </div>
                    </div>
                </button>
            </div>
        </div>
    )
}