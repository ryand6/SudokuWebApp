import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./auth/AuthProvider";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}


export default App
