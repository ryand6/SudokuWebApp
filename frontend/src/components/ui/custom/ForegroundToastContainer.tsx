import ReactDOM from "react-dom";
import { ToastContainer } from "react-toastify";

export function ForegroundToastContainer() {
    return ReactDOM.createPortal(
        <ToastContainer 
            position="top-right"
            containerId="foreground" 
            autoClose={5000} 
            style={{ zIndex: 1100 }} 
        />,
        document.body
    );
}