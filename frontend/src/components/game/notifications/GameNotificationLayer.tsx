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
    if (count >= 5) return 3;
    if (count >= 3) return 2;
    if (count >= 2) return 1;
    return 0;
}

const TEXT_STYLES = {
    positive: {
        color: "#DDB84A",
        WebkitTextStroke: "1px #8a6a00",
        textShadow: "1px 1px 0 #6b4f00, 2px 2px 0 rgba(80,50,0,0.4)",
        filter: "drop-shadow(0 2px 4px rgba(180,130,0,0.3))",
    },
    negative: {
        color: "#F5C4A0",
        WebkitTextStroke: "1px #8a4a20",
        textShadow: "1px 1px 0 #6b3010, 2px 2px 0 rgba(100,40,0,0.4)",
        filter: "drop-shadow(0 2px 4px rgba(168,84,50,0.3))",
    },
} as const;

const STREAK_STYLES: Record<0 | 1 | 2 | 3, React.CSSProperties> = {
    0: {
        color: "#F5C4A0",
        WebkitTextStroke: "1px #8a4a20",
        textShadow: "1px 1px 0 #6b3010, 2px 2px 0 rgba(100,40,0,0.4)",
        filter: "drop-shadow(0 2px 4px rgba(168,84,50,0.3))",
    },
    1: {
        color: "#DDB84A",
        WebkitTextStroke: "1px #8a6a00",
        textShadow: "1px 1px 0 #6b4f00, 2px 2px 0 rgba(80,50,0,0.4)",
    },
    2: {
        color: "#EFCA5A",
        WebkitTextStroke: "1px #8a6a00",
        textShadow: "1px 1px 0 #6b4f00, 2px 2px 0 rgba(80,50,0,0.4), 0 0 10px rgba(239,202,90,0.5)",
    },
    3: {
        color: "#FFD060",
        WebkitTextStroke: "1px #7A3a00",
        textShadow: "1px 1px 0 #6b3000, 2px 2px 0 rgba(100,40,0,0.4), 0 0 12px rgba(255,160,40,0.6)",
        filter: "drop-shadow(0 0 8px rgba(255,100,0,0.6))",
    },
} as const;

const STREAK_CLASSES: Record<0 | 1 | 2 | 3, string> = {
    0: "animate-snap-in",
    1: "animate-snap-in animate-wiggle",
    2: "animate-snap-in animate-wiggle animate-shimmer-pulse",
    3: "animate-snap-in animate-wiggle animate-inferno-pulse",
};

// const STREAK_BACKING: Record<0 | 1 | 2 | 3, React.CSSProperties> = {
//     0: { background: "rgba(20, 10, 0, 0.55)", borderRadius: "0.4rem", padding: "0.1rem 0.5rem" },
//     1: { background: "rgba(20, 10, 0, 0.55)", borderRadius: "0.4rem", padding: "0.1rem 0.5rem" },
//     2: { background: "rgba(20, 10, 0, 0.6)",  borderRadius: "0.4rem", padding: "0.1rem 0.5rem" },
//     3: { background: "rgba(40, 10, 0, 0.65)", borderRadius: "0.4rem", padding: "0.1rem 0.5rem" },
// };

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
    
    const showNotification = (notification.type === "score" && scoreNotificationsEnabled) || (notification.type === "streak" && streakNotificationsEnabled);

    if (!showNotification) return null;

    if (notification.type === "streak") {
        const tier = getStreakTier(notification.message);
        return (
            <div
                className={`py-2 px-2 font-extrabold text-lg font-mono ${STREAK_CLASSES[tier]}`}
                style={STREAK_STYLES[tier]}
                onAnimationEnd={handleAnimationEnd}
            >
                <span>
                    {notification.message}
                </span>
            </div>
        );
    }

    const isNegative = notification.message.includes("-") || notification.message.includes("lost");
    const textStyle = isNegative ? TEXT_STYLES.negative : TEXT_STYLES.positive;

    // const SCORE_BACKING: React.CSSProperties = {
    //     background: "rgba(20, 10, 0, 0.55)",
    //     borderRadius: "0.4rem",
    //     padding: "0.1rem 0.5rem",
    // };

    return (
        <div
            className="py-2 px-2 font-extrabold text-lg font-mono animate-snap-in"
            style={textStyle}
            onAnimationEnd={handleAnimationEnd}
        >
            <span>
                {notification.message}
            </span>
        </div>
    )
}