import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthContextProvider } from "./auth/AuthContextProvider";
import { PostLoginRedirectHandler } from "./auth/PostLoginRedirectHandler";

function App() {
  return (
    <BrowserRouter>
      <AuthContextProvider>
        <PostLoginRedirectHandler>
          <Routes>
          </Routes>
        </PostLoginRedirectHandler>
      </AuthContextProvider>
    </BrowserRouter>
  );
}


export default App
