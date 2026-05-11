import { Button } from "../button";
import { Spinner } from "../spinner";

export function SpinnerButton({
    text = "Loading..."
}: {
    text?: string
}) {
    return (
        <div className="flex flex-col items-center gap-4">
            <Button disabled size="sm">
                <Spinner />
                {text}
            </Button>
        </div>
    )
}