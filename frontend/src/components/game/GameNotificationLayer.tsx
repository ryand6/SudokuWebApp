import { useGameNotifications } from "@/hooks/notifications/useGameNotifications";
import { notificationClassNameSelect, type GameNotification } from "@/utils/game/gameNotificationUtils";

export function GameNotificationLayer() {
    const notifications = useGameNotifications();

    return (
        <div 
            className="fixed top-1.5 left-[50%] translate-x-[50%] 
                        flex flex-col items-center gap-0.5 z-50 
                        pointer-events-none"
        >
            {notifications.map((n) => (
                <NotificationBadge key={n.id} notification={n} />
            ))}
        </div>
    )
}

function NotificationBadge({ notification }: { notification: GameNotification }) {
    return (
        <div 
            className={`py-2 px-5 border-r-8 shadow-md 
                        font-bold animate-float-up
                        ${notificationClassNameSelect[notification.type]}`}    
        >
            {notification.message}
        </div>
    )
}