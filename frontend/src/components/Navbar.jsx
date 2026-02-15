import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
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
  if (roles.includes('ADMIN')) homepageLink = '/admin/locatii';
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
            <Link to="/admin/locatii">Administrare Locații</Link>
            <Link to="/admin/servicii">Administrare Servicii</Link>
          </>
        )}

        {/* PACIENT */}
        {roles.includes('PACIENT') && (
          <>
            <Link to="/pacient/profil">Profil</Link>
            <Link to="/pacient/jurnal">Jurnal</Link>
          </>
        )}

        {/* TERAPEUT */}
        {roles.includes('TERAPEUT') && (
          <>
            <Link to="/terapeut/profil">Profil</Link>
            <Link to="/terapeut/evaluari">Evaluari</Link>
            <Link to="/terapeut/evolutii">Evolutii</Link>
          </>
        )}

        <button className="nav-logout-btn" onClick={handleLogout}>Deconectare</button>
      </div>
    </nav>
  );
}