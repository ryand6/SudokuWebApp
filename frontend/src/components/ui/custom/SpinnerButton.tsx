import { Button } from "../button";
import { Spinner } from "../spinner";

export function SpinnerButton() {
    return (
        <div className="flex flex-col items-center gap-4">
            <Button disabled size="sm">
                <Spinner />
                Loading...
            </Button>
        </div>
    )
}