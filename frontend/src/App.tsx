import { BrowserRouter, Routes, Route } from "react-router-dom";
import { UserSetupPage } from "./pages/UserSetupPage";
import { HomePage } from "./pages/HomePage";
import { RequireAuth } from "./auth/RequireAuth";
import { DashboardPage } from "./pages/DashboardPage";
import { UserAmendPage } from "./pages/UserAmendPage";
import { NewUserOnly } from "./auth/NewUserOnly";
import { ToastContainer } from "react-toastify";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { WebSocketProvider } from "./context/WebSocketProvider";
import { CreateLobbyPage } from "./pages/CreateLobbyPage";
import { LobbyPage } from "./pages/LobbyPage";

// Manages cache, retries, queries etc.
const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient} >
      <WebSocketProvider>
        <BrowserRouter>
          <Routes>
            {/* Public routes */}
            <Route path="/" element={<HomePage />} />
            {/* One time public routes - new users only */}
            <Route path="/user-setup" element={<NewUserOnly><UserSetupPage /></NewUserOnly>} />
            {/* Protected routes */}
            <Route path="/dashboard" element={<RequireAuth><DashboardPage /></RequireAuth>} />
            <Route path="/user-update" element={<RequireAuth><UserAmendPage /></RequireAuth>} />
            <Route path="/create-lobby" element={<RequireAuth><CreateLobbyPage /></RequireAuth>} />
            <Route path="/lobby/:lobbyId" element={<RequireAuth><LobbyPage /></RequireAuth>} />
          </Routes>
          <ToastContainer position="top-right" autoClose={5000} />
        </BrowserRouter>
      </WebSocketProvider>
    </QueryClientProvider>
  );
}


export default App
