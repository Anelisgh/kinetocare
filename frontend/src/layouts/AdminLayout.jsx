import { Outlet } from 'react-router-dom';
import Navbar from '../components/shared/Navbar';

export default function AdminLayout() {
  return (
    <div className="app-container">
      <Navbar />
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}