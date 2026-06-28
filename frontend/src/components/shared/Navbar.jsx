import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import NotificationBell from './NotificationBell';
import '../../styles/navbar.css';

export default function Navbar() {
  const { userInfo, logout } = useAuth();
  const navigate = useNavigate();
  const roles = userInfo?.roles || [];
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
    setIsMenuOpen(false);
  };

  const toggleMenu = () => {
    setIsMenuOpen(prev => !prev);
  };

  const closeMenu = () => {
    setIsMenuOpen(false);
  };

  let homepageLink = '/login';
  if (roles.includes('ADMIN')) homepageLink = '/admin/homepage';
  else if (roles.includes('TERAPEUT')) homepageLink = '/terapeut/homepage';
  else if (roles.includes('PACIENT')) homepageLink = '/pacient/homepage';

  return (
    <nav className="navbar">
      <div className="logo">
        <Link to={homepageLink} onClick={closeMenu}>
          <img src="/images/Logo.png" alt="Logo Kinetocare" />
        </Link>
      </div>

      <button 
        className={`hamburger ${isMenuOpen ? 'active' : ''}`} 
        onClick={toggleMenu} 
        aria-label="Meniu"
        aria-expanded={isMenuOpen}
      >
        <span className="bar"></span>
        <span className="bar"></span>
        <span className="bar"></span>
      </button>

      <div className={`menu ${isMenuOpen ? 'open' : ''}`}>
        {/* ADMIN */}
        {roles.includes('ADMIN') && (
          <>
            <Link to="/admin/locatii" onClick={closeMenu}>Locații</Link>
            <Link to="/admin/servicii" onClick={closeMenu}>Servicii</Link>
            <Link to="/admin/utilizatori" onClick={closeMenu}>Utilizatori</Link>
            <Link to="/admin/statistici" onClick={closeMenu}>Statistici</Link>
          </>
        )}

        {/* PACIENT */}
        {roles.includes('PACIENT') && (
          <>
            <Link to="/pacient/homepage" onClick={closeMenu}>🏠 Acasă</Link>
            <Link to="/pacient/programari" onClick={closeMenu}>📅 Programări</Link>
            <Link to="/pacient/jurnal" onClick={closeMenu}>📖 Jurnal</Link>
            <Link to="/chat-pacient" onClick={closeMenu}>💬 Mesaje</Link>
          </>
        )}

        {/* TERAPEUT */}
        {roles.includes('TERAPEUT') && (
          <>
            <Link to="/terapeut/homepage" onClick={closeMenu}>🏠 Acasă</Link>
            <Link to="/terapeut/pacienti" onClick={closeMenu}>👥 Pacienți</Link>
            <Link to="/terapeut/evaluari" onClick={closeMenu}>📝 Evaluări</Link>
            <Link to="/terapeut/evolutii" onClick={closeMenu}>📈 Evoluții</Link>
            <Link to="/chat-terapeut" onClick={closeMenu}>💬 Mesaje</Link>
          </>
        )}

        {/* NOTIFICARI - vizibile pentru PACIENT si TERAPEUT */}
        {(roles.includes('PACIENT') || roles.includes('TERAPEUT')) && (
          <>
            <NotificationBell />
            {roles.includes('PACIENT') && <Link to="/pacient/profil" onClick={closeMenu}>👤 Profil</Link>}
            {roles.includes('TERAPEUT') && <Link to="/terapeut/profil" onClick={closeMenu}>👤 Profil</Link>}
          </>
        )}

        {roles.includes('ADMIN') && (
          <button className="nav-logout-btn" onClick={handleLogout}>Deconectare</button>
        )}
      </div>
    </nav>
  );
}
