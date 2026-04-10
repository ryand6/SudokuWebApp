import React from "react";
import ReactDOM from "react-dom";
import { Button } from "../button";

interface ModalProps {
    isOpen: boolean,
    onClose: () => void,
    children: React.ReactElement,
    className?: string
}

export function Modal({ isOpen, onClose, children, className }: ModalProps) {
    if (!isOpen) return null;
    return ReactDOM.createPortal(
        <>
            <div 
                className="fixed inset-0 z-999"
                onClick={onClose}
            />
            <div 
                className={`fixed w-full top-0 left-0 h-full 
                            sm:top-[5%] sm:left-[5%] sm:w-[90%] sm:h-[90%]
                            bg-card z-1000 flex border-[2px] border-border rounded-md
                            ${className}`}
            >
                <div className="flex w-[100%] h-[100%] relative">
                    <Button 
                        className="absolute cursor-pointer text-xl px-4 top-2 right-2 bg-accent/0 text-foreground 
                                    hover:font-extrabold hover:bg-primary/80 hover:text-primary-foreground z-[1000]"
                        onClick={onClose}
                    >
                        &times;
                    </Button>
                    {children}
                </div>
            </div>
        </>
        ,
        // render outside app root
        document.body
    )
}