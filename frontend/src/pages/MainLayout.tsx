import { Outlet } from "react-router-dom";

export default function MainLayout() {
  return (
    <div className="min-h-screen flex flex-col">
      <header className="bg-header text-header-foreground px-4 py-3">
        <div className="max-w-6xl mx-auto flex items-center justify-between">
          <h1 className="font-bold text-lg">
            Tomo Sudoku
          </h1>
        </div>
      </header>

      <main className="flex-1">
        <Outlet />
      </main>

      <footer className="bg-footer text-footer-foreground px-4 py-2 text-sm text-center">
        © RD
      </footer>

    </div>
  );
}