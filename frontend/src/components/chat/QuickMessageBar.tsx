import type { Dispatch, SetStateAction } from "react"

export function QuickMessageBar({
    messageTemplates,
    setInputMessage,
    handleClick
}: {
    messageTemplates: string[],
    setInputMessage: Dispatch<SetStateAction<string>>,
    handleClick: () => void
}) {
    return (
        <div className="flex flex-wrap gap-1.5">
            {
                messageTemplates.map((template) => (
                    <div key={template} 
                        className="cursor-pointer py-2 px-3 font-display bg-primary text-primary-foreground text-lg rounded-full"
                        onClick={() => {
                            setInputMessage(template);
                            handleClick();
                        }}
                    >
                        {template}
                    </div>
                ))
            }
        </div>
    )
}