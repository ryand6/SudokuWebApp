import React from "react";
import ReactDOM from "react-dom";
import { Button } from "./ui/button";

interface ModalProps {
    isOpen: boolean,
    onClose: () => void,
    children: React.ReactElement
}

export function Modal({ isOpen, onClose, children }: ModalProps) {
    if (!isOpen) return null;
    return ReactDOM.createPortal(
        <div className="fixed top-[10%] left-[10%] w-[80%] h-[80%] bg-white z-1000 flex border-[2px] border-border rounded-md">
            <div className="flex w-[100%] h-[100%] relative">
                <Button 
                    className="absolute cursor-pointer top-2 right-2 text-dark-gray text-[25px] hover:font-bold"
                    onClick={onClose}
                    variant="secondary"
                >
                    &times;
                </Button>
                {children}
            </div>
        </div>,
        // render outside app root
        document.body
    )
}