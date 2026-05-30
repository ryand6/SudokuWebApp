import { useGameNotifications } from "@/hooks/notifications/useGameNotifications";
import { notificationClassNameSelect, type GameNotification } from "@/utils/game/gameNotificationUtils";

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
            className="fixed top-[1vh] left-[50%] translate-x-[50%] 
                        flex flex-col items-center gap-0.5 z-50 
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
}) {

    const showNotification = (notification.type === "score" && scoreNotificationsEnabled) || (notification.type === "streak" && streakNotificationsEnabled);

    if (!showNotification) return null;

    return (
        <div 
            className={`py-2 px-5 rounded-full shadow-xl 
                        font-bold animate-float-up
                        ${notificationClassNameSelect[notification.type]}`}    
        >
            {notification.message}
        </div>
    )
}