import { Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar';

export default function TerapeutLayout() {
  return (
    <div className="terapeut-layout">
      <Navbar />
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}