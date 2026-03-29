export type NotificationType = "score" | "timer" | "streak";

export type GameNotification = {
    id: string,
    type: NotificationType,
    message: string
}

type GameNotificationListener = (notification: GameNotification) => void;

const gameNotificationListeners: Set<GameNotificationListener>  = new Set();

export const notificationEmitter = {
    // id ommitted from object shape and created inside emit
    emit(notification: Omit<GameNotification, "id">) {
        const notificationWithId = {...notification, id: crypto.randomUUID()};
        gameNotificationListeners.forEach((fn) => fn(notificationWithId));
    },
    subscribe(fn: GameNotificationListener) {
        gameNotificationListeners.add(fn);
        // Returns cleanup method
        return () => { 
            gameNotificationListeners.delete(fn); 
        }
    }
}

export const notificationClassNameSelect: Record<NotificationType, string> = {
    score: "bg-yellow-400 text-black",
    timer: "bg-blue-400 text-white",
    streak: "bg-orange-400 text-black"
}