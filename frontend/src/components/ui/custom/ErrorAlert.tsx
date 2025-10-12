import { AlertCircleIcon } from "lucide-react";
import { Alert, AlertTitle } from "../alert";

export function ErrorAlert() {
    return (
        <div className="items-center gap-4">
            <Alert variant="destructive">
                <AlertCircleIcon />
                <AlertTitle>Unexpected error occurred.</AlertTitle>
            </Alert>
        </div>
    )
}