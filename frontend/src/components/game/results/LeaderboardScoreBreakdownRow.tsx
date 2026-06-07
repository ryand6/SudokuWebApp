export function LeaderboardScoreBreakdownRow({
    operator,
    label,
    value,
    sublabel,
    subvalue,
    isFinal = false,
}: {
    operator: string;
    label: string;
    value: string;
    sublabel?: string;
    subvalue?: string;
    isFinal?: boolean;
}) {
    return (
        <div
            className={`flex items-center justify-between px-3 py-2 border-b border-muted last:border-b-0 ${
                isFinal ? "bg-sidebar" : ""
            }`}
        >
            <div className="flex items-center gap-2">
                <span
                    className={`text-xl font-semibold w-5 text-center font-display ${
                        isFinal ? "text-sidebar-primary" : "text-primary"
                    }`}
                >
                    {operator}
                </span>
                <div>
                    <div
                        className={`text-md font-sans ${
                            isFinal
                                ? "text-sidebar-foreground font-bold text-lg"
                                : "text-accent-foreground"
                        }`}
                    >
                        {label}
                    </div>
                    {sublabel && (
                        <div
                            className="text-md text-muted-foreground font-sans"
                        >
                            {sublabel}
                        </div>
                    )}
                </div>
            </div>
            <div className="text-right">
                <div
                    className={`font-semibold font-display ${
                        isFinal
                            ? "text-sidebar-primary text-3xl"
                            : "text-foreground text-lg"
                    }`}
                >
                    {value}
                </div>
                {subvalue && (
                    <div
                        className="text-md text-muted-foreground font-sans"
                    >
                        {subvalue}
                    </div>
                )}
            </div>
        </div>
    );
}
