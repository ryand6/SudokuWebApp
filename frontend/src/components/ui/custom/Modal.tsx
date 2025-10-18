import React from "react";
import ReactDOM from "react-dom";
import { Button } from "../button";

interface ModalProps {
    isOpen: boolean,
    onClose: () => void,
    children: React.ReactElement
}

export function Modal({ isOpen, onClose, children }: ModalProps) {
    if (!isOpen) return null;
    return ReactDOM.createPortal(
        <div className="fixed w-full top-0 left-0 h-full sm:top-[5%] sm:left-[5%] sm:w-[90%] sm:h-[90%] bg-white z-1000 flex border-[2px] border-border rounded-md">
            <div className="flex w-[100%] h-[100%] relative">
                <Button 
                    className="absolute cursor-pointer text-xl px-4 top-1 right-1 text-dark-gray hover:font-extrabold hover:text-primary z-[1000]"
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