export function BulletList({
    children,
}: {
    children: React.ReactNode;
}) {
    return (
        <ul className="flex flex-col gap-2 pl-6 list-disc marker:text-primary">
            {children}
        </ul>
    );
}