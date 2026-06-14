import type { Dispatch, SetStateAction } from "react"

export function QuickMessageBar({
    messageTemplates,
    setInputMessage,
    handleClick
}: {
    messageTemplates: string[],
    setInputMessage: Dispatch<SetStateAction<string>>,
    handleClick: (message: string) => void
}) {
    return (
        <div className="flex flex-wrap gap-1.5">
            {
                messageTemplates.map((template) => (
                    <div key={template} 
                        className="cursor-pointer py-2 px-3 font-display bg-primary text-primary-foreground text-lg rounded-full hover:bg-primary/70"
                        onClick={() => {
                            setInputMessage(template);
                            handleClick(template);
                        }}
                    >
                        {template}
                    </div>
                ))
            }
        </div>
    )
}