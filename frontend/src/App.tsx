import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthContextProvider } from "./auth/AuthContextProvider";
import { UserSetupPage } from "./pages/UserSetupPage";
import { HomePage } from "./pages/HomePage";
import { RequireAuth } from "./auth/RequireAuth";
import { DashboardPage } from "./pages/DashboardPage";
import { UserAmendPage } from "./pages/UserAmendPage";

function App() {
  return (
    <BrowserRouter>
      <AuthContextProvider>
        <Routes>
          {/* Public routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/user-setup" element={<UserSetupPage />} />

          {/* Protected routes */}
          <Route
            path="/dashboard"
            element={
              <RequireAuth>
                <DashboardPage />
              </RequireAuth>
            }
          />
          <Route
            path="/user-update"
            element={
              <RequireAuth>
                <UserAmendPage />
              </RequireAuth>
            }
          />
        </Routes>
      </AuthContextProvider>
    </BrowserRouter>
  );
}


export default App
