import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"

export function ReturnToLobbyAlertDialog({
    open, 
    handleContinueClick, 
    setOpen
}: {
    open: boolean, 
    handleContinueClick: () => void, 
    setOpen: React.Dispatch<React.SetStateAction<boolean>>
}) {
    if (open) {
        document.getElementById("game-result-modal")?.classList.add("blur-sm");
    } else {
        document.getElementById("game-result-modal")?.classList.remove("blur-sm");
    }

    return (
        <AlertDialog open={open} onOpenChange={setOpen}>
            <AlertDialogContent className="bg-card z-[1000]">
                <AlertDialogHeader>
                    <AlertDialogTitle>Are you sure you want to return to the lobby?</AlertDialogTitle>
                </AlertDialogHeader>
                <AlertDialogDescription>This dialog confirms your intention to return to the lobby.</AlertDialogDescription>
                <AlertDialogFooter>
                    <AlertDialogCancel className="cursor-pointer">Cancel</AlertDialogCancel>
                    <AlertDialogAction className="cursor-pointer" onClick={handleContinueClick}>Continue</AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    )
}