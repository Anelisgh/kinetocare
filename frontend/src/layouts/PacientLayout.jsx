import { Outlet } from 'react-router-dom';
import Navbar from '../components/shared/Navbar';

export default function PacientLayout() {
  return (
    <div className="pacient-layout">
      <Navbar />
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}