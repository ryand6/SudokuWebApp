import type { Dispatch, SetStateAction } from "react";
import { IconPencil } from '@tabler/icons-react';
import { IconMessageBolt } from '@tabler/icons-react';

export function MessageTypeSelectorRow({
    isQuickMessage,
    setIsQuickMessage,
    isMobile
}: {
    isQuickMessage: boolean,
    setIsQuickMessage: Dispatch<SetStateAction<boolean>>,
    isMobile: boolean
}) {

    const iconSize: number = isMobile ? 16 : 24;
    const iconStroke: number = isMobile ? 2 : 3;

    return (
        <div className="flex justify-between py-3">
            <div className="font-display text-muted-foreground font-semibold tracking-widest text-md md:text-lg">
                SEND A MESSAGE
            </div>
            <div className="flex p-1 gap-1 rounded-full border-muted border-1 bg-background">
                <div 
                    className={`p-3 py-2 font-display rounded-full cursor-pointer text-sidebar-primary-foreground flex items-center gap-2
                                ${!isQuickMessage && "bg-secondary! text-secondary-foreground! font-semibold!"}`}
                    onClick={() => setIsQuickMessage(false) }
                >
                    <span><IconPencil size={iconSize} /></span>
                    <span>Type</span>
                </div>
                <div 
                    className={`p-3 py-2 font-display rounded-full cursor-pointer text-sidebar-primary-foreground flex items-center gap-2
                                 ${isQuickMessage && "bg-secondary! text-secondary-foreground! font-semibold!"}`}
                    onClick={() => setIsQuickMessage(true)} 
                >
                    <span><IconMessageBolt size={iconSize} /></span>
                    <span>Quick</span>
                </div>
            </div>
        </div>
    )
}