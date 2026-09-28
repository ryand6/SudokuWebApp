import type { IconProps } from "@tabler/icons-react";
import type { ForwardRefExoticComponent, RefAttributes } from "react";

export function Section({
    icon: Icon,
    title,
    isMobile,
    children,
}: {
    icon: ForwardRefExoticComponent<IconProps & RefAttributes<SVGSVGElement>>,
    title: string,
    isMobile: boolean,
    children: React.ReactNode
}) {
    const iconSize = isMobile ? 12 : 24;

    return (
        <section className="flex flex-col gap-4">
            <div className="flex items-center gap-3">
                <span className="flex shrink-0 items-center justify-center rounded-xl bg-primary/10 p-2.5 text-primary">
                    <Icon size={iconSize} />
                </span>
                <h2 className="text-xl font-semibold tracking-wide text-foreground">
                    {title}
                </h2>
            </div>
            <div className="flex flex-col gap-4 text-base leading-7 text-foreground/90">
                {children}
            </div>
        </section>
    );
}