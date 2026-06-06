import { useGameNotifications } from "@/hooks/notifications/useGameNotifications";
import { type GameNotification } from "@/utils/game/gameNotificationUtils";
import { useState, type Dispatch, type JSX, type SetStateAction } from "react";

export function GameNotificationLayer({
    scoreNotificationsEnabled,
    streakNotificationsEnabled
}: {
    scoreNotificationsEnabled: boolean | undefined,
    streakNotificationsEnabled: boolean | undefined
}) {
    const { notifications, dismiss } = useGameNotifications();

    const handleAnimationEnd = () => {
        dismiss(notifications[0].id);  
    }

    if (scoreNotificationsEnabled === undefined || streakNotificationsEnabled === undefined) return null;

    return (
        <div 
            className="max-h-8 flex items-center justify-center z-50 pointer-events-none"
        >
            {
                notifications[0] && 
                    <NotificationBadge 
                        key={notifications[0].id}
                        notification={notifications[0]} 
                        scoreNotificationsEnabled={scoreNotificationsEnabled} 
                        streakNotificationsEnabled={streakNotificationsEnabled}
                        handleAnimationEnd={handleAnimationEnd}
                    />
            }
            
        </div>
    )
}

function NotificationBadge({ 
    notification,
    scoreNotificationsEnabled,
    streakNotificationsEnabled,
    handleAnimationEnd
}: { 
    notification: GameNotification,
    scoreNotificationsEnabled: boolean,
    streakNotificationsEnabled: boolean,
    handleAnimationEnd: () => void
}): JSX.Element | null {

    console.log("scoreNotificationsEnabled: ", scoreNotificationsEnabled);
    console.log("streakNotificationsEnabled: ", streakNotificationsEnabled);

    const isNegative = notification.message.includes("-");
    
    const showNotification = (notification.type === "score" && scoreNotificationsEnabled) || (notification.type === "streak" && streakNotificationsEnabled);

    if (!showNotification) return null;

    return (
        <div 
            className={`py-2 px-2 font-extrabold animate-snap-in text-primary font-mono`} 
            onAnimationEnd={handleAnimationEnd}          
        >
            <div className={`${notification.type === "streak" && "animate-wiggle"}`}>
                <span 
                    className={`${isNegative && "text-destructive"}`}
                >
                    {notification.message}
                </span>
            </div>

        </div>
    )
}