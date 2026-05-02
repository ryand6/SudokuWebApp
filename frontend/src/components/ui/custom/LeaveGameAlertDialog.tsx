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

export function LeaveGameAlertDialog({
    open, 
    handleContinueClick, 
    setOpen
}: {
    open: boolean, 
    handleContinueClick: () => void, 
    setOpen: React.Dispatch<React.SetStateAction<boolean>>
}) {
    return (
        <AlertDialog open={open} onOpenChange={setOpen}>
            <AlertDialogContent className="bg-card">
                <AlertDialogHeader>
                    <AlertDialogTitle>Are you sure you want to leave the game?</AlertDialogTitle>
                    <AlertDialogDescription>
                        Leaving the game will forfeit your score and count as a loss. You will be removed from the lobby and returned to the dashboard.
                    </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel className="cursor-pointer">Cancel</AlertDialogCancel>
                    <AlertDialogAction className="cursor-pointer" onClick={handleContinueClick}>Continue</AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    )
}