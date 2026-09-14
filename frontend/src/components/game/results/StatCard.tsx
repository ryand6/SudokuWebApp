export function StatCard({ 
    value, 
    label,
    compact
}: { 
    value: string,
    label: string,
    compact: boolean
}) {
    return (
        <div className="bg-card border-2 border-muted rounded-md px-2.5 py-2 text-center">
            <div
                className={`${compact ? "text-md" : "text-xl"} font-semibold text-primary leading-none mb-0.5 font-display`}
            >
                {value}
            </div>
            <div
                className={`${compact ? "text-xs" : "text-md"} text-muted-foreground font-sans`}
            >
                {label}
            </div>
        </div>
    );
}
