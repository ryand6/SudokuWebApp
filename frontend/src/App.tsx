import { BrowserRouter, Routes, Route } from "react-router-dom";
import { UserSetupPage } from "./pages/UserSetupPage";
import { HomePage } from "./pages/HomePage";
import { RequireAuth } from "./auth/RequireAuth";
import { DashboardPage } from "./pages/DashboardPage";
import { UsernameUpdatePage } from "./pages/UsernameUpdatePage";
import { NewUserOnly } from "./auth/NewUserOnly";
import { ToastContainer } from "react-toastify";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { WebSocketProvider } from "./context/WebSocketProvider";
import { CreateLobbyPage } from "./pages/CreateLobbyPage";
import { LobbyPage } from "./pages/LobbyPage";
import { PrivateLobbyJoinPage } from "./pages/PrivateLobbyJoinPage";
import { GamePage } from "./pages/GamePage";
import MainLayout from "./pages/MainLayout";
import { ForegroundToastContainer } from "./components/ui/custom/ForegroundToastContainer";
import { LinkAccountPage } from "./pages/LinkAccountPage";
import { WsDevToolbar } from "./components/testing/WsDevToolBar";
import { LeaderboardsPage } from "./pages/LeaderboardsPage";
import { LinkAdditionalProvidersPage } from "./pages/LinkAdditionalProvidersPage";
import { RecoveryEmailUpdatePage } from "./pages/RecoveryEmailUpdatePage";
import { PrivacyPolicyPage } from "./pages/PrivacyPolicyPage";

// Manages cache, retries, queries etc.
const queryClient = new QueryClient();

function App() {
  return (
    <>
      <QueryClientProvider client={queryClient} >
        <WebSocketProvider>
          <BrowserRouter>
            <Routes>
              <Route element={<MainLayout />}>
                {/* Public routes */}
                <Route path="/" element={<HomePage />} />
                <Route path="/link-account" element={<LinkAccountPage />} />
                <Route path="/privacy-policy" element={<PrivacyPolicyPage />} />
                {/* One time public routes - new users only */}
                <Route path="/user-setup" element={<NewUserOnly><UserSetupPage /></NewUserOnly>} />
                {/* Protected routes */}
                <Route path="/dashboard" element={<RequireAuth><DashboardPage /></RequireAuth>} />
                <Route path="/link-additional-providers" element={<RequireAuth><LinkAdditionalProvidersPage /></RequireAuth>} />
                <Route path="/leaderboards" element={<RequireAuth><LeaderboardsPage /></RequireAuth>}/>
                <Route path="/username-update" element={<RequireAuth><UsernameUpdatePage /></RequireAuth>} />
                <Route path="/recovery-email-update" element={<RequireAuth><RecoveryEmailUpdatePage /></RequireAuth>} />
                <Route path="/create-lobby" element={<RequireAuth><CreateLobbyPage /></RequireAuth>} />
                <Route path="/lobby/:lobbyId" element={<RequireAuth><LobbyPage /></RequireAuth>} />
                <Route path="/lobby/private/:token" element={<RequireAuth><PrivateLobbyJoinPage /></RequireAuth>} />
                <Route path="/game/:gameId" element={<RequireAuth><GamePage /></RequireAuth>} />
              </Route>
            </Routes>
            <ToastContainer position="top-right" containerId="default" autoClose={5000} />
          </BrowserRouter>
          {/* {import.meta.env.DEV && <WsDevToolbar />} */}
        </WebSocketProvider>
      </QueryClientProvider>
      <ForegroundToastContainer />
    </>

  );
}


export default App
