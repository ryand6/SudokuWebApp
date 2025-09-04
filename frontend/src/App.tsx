import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthContextProvider } from "./auth/AuthContextProvider";
import { UserSetupPage } from "./pages/UserSetupPage";
import { HomePage } from "./pages/HomePage";
import { RequireAuth } from "./auth/RequireAuth";
import { DashboardPage } from "./pages/DashboardPage";
import { UserAmendPage } from "./pages/UserAmendPage";
import { NewUserOnly } from "./auth/NewUserOnly";
import { ToastContainer } from "react-toastify";

function App() {
  return (
    <BrowserRouter>
      <AuthContextProvider>
        <Routes>
          {/* Public routes */}
          <Route path="/" element={<HomePage />} />

          {/* One time public routes - new users only */}
          <Route path="/user-setup" element={
            <NewUserOnly>
                <UserSetupPage />
            </NewUserOnly>
            } 
          />

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
      <ToastContainer position="top-right" autoClose={5000} />
    </BrowserRouter>
  );
}


export default App
