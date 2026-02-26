import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import NotificationBell from './NotificationBell';
import '../styles/navbar.css';

export default function Navbar() {
  const { userInfo, logout } = useAuth();
  const navigate = useNavigate();
  const roles = userInfo?.roles || [];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  let homepageLink = '/login';
  if (roles.includes('ADMIN')) homepageLink = '/admin/homepage';
  else if (roles.includes('TERAPEUT')) homepageLink = '/terapeut/homepage';
  else if (roles.includes('PACIENT')) homepageLink = '/pacient/homepage';

  return (
    <nav className="navbar">
      <div className="logo">
        <Link to={homepageLink}>
          <img src="/images/Logo.png" alt="Logo Kinetocare" />
          <h1>Kinetocare</h1>
        </Link>
      </div>

      <div className="menu">
        {/* ADMIN */}
        {roles.includes('ADMIN') && (
          <>
            <Link to="/admin/locatii">Locații</Link>
            <Link to="/admin/servicii">Servicii</Link>
            <Link to="/admin/utilizatori">Utilizatori</Link>
            <Link to="/admin/statistici">Statistici</Link>
          </>
        )}

        {/* PACIENT */}
        {roles.includes('PACIENT') && (
          <>
            <Link to="/pacient/programari">Programări</Link>
            <Link to="/pacient/jurnal">Jurnal</Link>
            <Link to="/pacient/profil">Profil</Link>
            <Link to="/chat-pacient">Chat</Link>
          </>
        )}

        {/* TERAPEUT */}
        {roles.includes('TERAPEUT') && (
          <>
            <Link to="/terapeut/pacienti">Pacienți</Link>
            <Link to="/terapeut/evaluari">Evaluari</Link>
            <Link to="/terapeut/evolutii">Evolutii</Link>
            <Link to="/terapeut/profil">Profil</Link>
            <Link to="/chat-terapeut">Chat</Link>
          </>
        )}

        {/* NOTIFICARI - vizibile pentru PACIENT si TERAPEUT */}
        {(roles.includes('PACIENT') || roles.includes('TERAPEUT')) && (
          <NotificationBell />
        )}

        <button className="nav-logout-btn" onClick={handleLogout}>Deconectare</button>
      </div>
    </nav>
  );
}