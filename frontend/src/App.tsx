import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthContextProvider } from "./auth/AuthContextProvider";

function App() {
  return (
    <BrowserRouter>
      <AuthContextProvider>
        <Routes>
        </Routes>
      </AuthContextProvider>
    </BrowserRouter>
  );
}


export default App
