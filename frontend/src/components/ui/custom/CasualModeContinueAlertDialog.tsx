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

export function CasualModeContinueAlertDialog({
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
                    <AlertDialogTitle>Are you sure you want to continue playing in casual mode?</AlertDialogTitle>
                    <AlertDialogDescription>
                        You can finish the board at your own pace, but scores will not be counted whilst in casual mode.
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