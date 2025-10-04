import React from "react";
import ReactDOM from "react-dom";

interface ModalProps {
    isOpen: boolean,
    onClose: () => void,
    children: React.ReactElement
}

export function Modal({ isOpen, onClose, children }: ModalProps) {
    if (!isOpen) return null;
    return ReactDOM.createPortal(
        <div className="fixed top-[10%] left-[10%] w-[80%] h-[80%] bg-white z-1000 flex">
            <div className="flex w-[100%] h-[100%] relative">
                <button 
                    className="absolute top-2 right-2 text-gray-500 hover:text-black"
                    onClick={onClose}
                >
                    &times;
                </button>
                {children}
            </div>
        </div>,
        // render outside app root
        document.body
    )
}