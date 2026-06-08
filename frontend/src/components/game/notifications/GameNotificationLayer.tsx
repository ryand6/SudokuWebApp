import { useGameNotifications } from "@/hooks/notifications/useGameNotifications";
import { type GameNotification } from "@/utils/game/gameNotificationUtils";
import { type JSX } from "react";

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

function getStreakTier(message: string): 0 | 1 | 2 | 3 {
    const match = message.match(/×(\d+)/);
    const count = match ? parseInt(match[1]) : 0;
    if (count >= 4) return 3;
    if (count >= 3) return 2;
    if (count >= 2) return 1;
    return 0;
}

const TEXT_STYLES = {
    positive: {
        color: "#DDB84A",
        textShadow: "1px 1px 0 #6b4f00, 2px 2px 0 rgba(80,50,0,0.4)",
        filter: "drop-shadow(0 2px 4px rgba(180,130,0,0.3))",
    },
    negative: {
        color: "#F5C4A0",
        textShadow: "1px 1px 0 #6b3010, 2px 2px 0 rgba(100,40,0,0.4)",
        filter: "drop-shadow(0 2px 4px rgba(168,84,50,0.3))",
    },
} as const;

const STREAK_STYLES: Record<0 | 1 | 2 | 3, React.CSSProperties> = {
    0: {
        color: "#F5C4A0",
        textShadow: "1px 1px 0 #6b3010, 2px 2px 0 rgba(100,40,0,0.4)",
        filter: "drop-shadow(0 2px 4px rgba(168,84,50,0.3))",
    },
    1: {
        color: "#DDB84A",
        textShadow: "1px 1px 0 #6b4f00, 2px 2px 0 rgba(80,50,0,0.4)",
    },
    2: {
        color: "#EFCA5A",
        textShadow: "1px 1px 0 #6b4f00, 2px 2px 0 rgba(80,50,0,0.4), 0 0 12px rgba(239,202,90,0.8)",
        filter: "drop-shadow(0 0 6px rgba(239,202,90,0.5))"
    },
    3: {
        color: "#FFD060",
        textShadow: "1px 1px 0 #6b3000, 2px 2px 0 rgba(100,40,0,0.4), 0 0 12px rgba(255,160,40,0.6)",
        filter: "drop-shadow(0 0 8px rgba(255,100,0,0.6))"
    },
} as const;

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

    const showNotification = (notification.type === "score" && scoreNotificationsEnabled) || (notification.type === "streak" && streakNotificationsEnabled);

    if (!showNotification) return null;

    const isNegative = notification.message.includes("-") || notification.message.includes("lost");

    if (notification.type === "streak") {
        const tier = getStreakTier(notification.message);
        return (
            <div
                className={`px-2 font-extrabold text-xl font-mono ${isNegative ? "animate-snap-in" : "animate-wiggle"}`}
                style={STREAK_STYLES[tier]}
                onAnimationEnd={handleAnimationEnd}
            >
                <span 
                    className={`${isNegative && "text-md"}`}
                >
                    {notification.message}
                </span>
            </div>
        );
    }

    const textStyle = isNegative ? TEXT_STYLES.negative : TEXT_STYLES.positive;

    return (
        <div
            className="px-2 font-extrabold text-xl font-mono animate-snap-in"
            style={textStyle}
            onAnimationEnd={handleAnimationEnd}
        >
            <span>
                {notification.message}
            </span>
        </div>
    )
}