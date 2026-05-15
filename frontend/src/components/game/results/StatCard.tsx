export function StatCard({ 
    value, 
    label 
}: { 
    value: string,
    label: string 
}) {
    return (
        <div className="bg-card border-2 border-muted rounded-md px-2.5 py-2 text-center">
            <div
                className="text-lg font-semibold text-primary leading-none mb-0.5 font-display"
            >
                {value}
            </div>
            <div
                className="text-xs text-muted-foreground font-sans"
            >
                {label}
            </div>
        </div>
    );
}
