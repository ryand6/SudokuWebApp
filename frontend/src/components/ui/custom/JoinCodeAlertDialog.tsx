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

export function JoinCodeAlertDialog({open, handleContinueClick, setOpen}: {open: boolean, handleContinueClick: () => void, setOpen: React.Dispatch<React.SetStateAction<boolean>>}) {
    return (
        <AlertDialog open={open} onOpenChange={setOpen}>
            <AlertDialogContent className="bg-card">
                <AlertDialogHeader>
                <AlertDialogTitle>Are you sure you want to generate a new join code?</AlertDialogTitle>
                <AlertDialogDescription>
                    A maximum of three join codes can be active at one time. Join codes will terminate either when used or once they have been active for 10 minutes.
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