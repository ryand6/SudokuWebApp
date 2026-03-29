import { notificationEmitter, type GameNotification } from "@/utils/game/gameNotificationUtils";
import { useEffect, useState } from "react";

export function useGameNotifications(dismissAfterMs = 2000) {
    const [notifications, setNotifications] = useState<GameNotification[]>([]);

    useEffect(() => {
        const unsubscribe = notificationEmitter.subscribe((notification) => {
            setNotifications((prev) => [...prev, notification]);

            setTimeout(() => {
                setNotifications((prev) => prev.filter((n) => n.id !== notification.id));
            }, dismissAfterMs);
        });

        return unsubscribe;
    }, [dismissAfterMs]);

    return notifications;
}