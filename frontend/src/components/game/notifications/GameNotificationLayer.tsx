import { useGameNotifications } from "@/hooks/notifications/useGameNotifications";
import { type GameNotification } from "@/utils/game/gameNotificationUtils";
import type { JSX } from "react";

export function GameNotificationLayer({
    scoreNotificationsEnabled,
    streakNotificationsEnabled
}: {
    scoreNotificationsEnabled: boolean | undefined,
    streakNotificationsEnabled: boolean | undefined
}) {
    const notifications = useGameNotifications();

    if (scoreNotificationsEnabled === undefined || streakNotificationsEnabled === undefined) return null;

    return (
        <div 
            className="fixed flex flex-col items-center gap-1 z-50 
                        pointer-events-none"
        >
            {notifications.map((n) => (
                <NotificationBadge 
                    key={n.id} 
                    notification={n} 
                    scoreNotificationsEnabled={scoreNotificationsEnabled} 
                    streakNotificationsEnabled={streakNotificationsEnabled} 
                />
            ))}
        </div>
    )
}

function NotificationBadge({ 
    notification,
    scoreNotificationsEnabled,
    streakNotificationsEnabled
}: { 
    notification: GameNotification,
    scoreNotificationsEnabled: boolean,
    streakNotificationsEnabled: boolean
}): JSX.Element | null {

    console.log("scoreNotificationsEnabled: ", scoreNotificationsEnabled);
    console.log("streakNotificationsEnabled: ", streakNotificationsEnabled);


    const isNegative = notification.message.includes("-");
    
    const showNotification = (notification.type === "score" && scoreNotificationsEnabled) || (notification.type === "streak" && streakNotificationsEnabled);

    if (!showNotification) return null;

    return (

        <div className="py-2 px-2 font-extrabold animate-float-up text-primary font-mono">
            <span 
                className={`${isNegative && "text-destructive"}
                            ${notification.type === "streak" && "animate-wiggle"}`}
            >
                {notification.message}
            </span>
        </div>


        
        
        // <div 
        //     className="py-2 px-5
        //                 font-bold animate-float-up"
                            
        // >
        //     {notification.message}
        // </div>
    )
}