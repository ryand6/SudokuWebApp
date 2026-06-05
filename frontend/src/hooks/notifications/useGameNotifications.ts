import { notificationEmitter, type GameNotification } from "@/utils/game/gameNotificationUtils";
import { useEffect, useState } from "react";

export function useGameNotifications() {
    const [notifications, setNotifications] = useState<GameNotification[]>([]);

    useEffect(() => {
        const unsubscribe = notificationEmitter.subscribe((notification) => {
            setNotifications((prev) => [...prev, notification]);
        });
        return unsubscribe;
    }, []);

    const dismiss = (id: string) => {
        setNotifications((prev) => prev.filter((n) => n.id !== id));
    };

    return { notifications, dismiss };
}