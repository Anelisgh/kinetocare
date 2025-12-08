import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext'; 

export default function HomepageTerapeut() {
  const { userInfo, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true }); // replace: true -> nu poti da back
  };

  if (!userInfo) {
    return <div className="auth-container">Încărcare...</div>;
  }

  return (
    <>
    <div className="auth-container">
      <h1>Bun venit, Terapeute!</h1>
      <div className="success-message">
        <p><strong>Email:</strong> {userInfo.email}</p>
        <p><strong>Roluri:</strong> {userInfo.roles.join(', ')}</p>
        <p><strong>Keycloak ID:</strong> {userInfo.keycloakId}</p>
      </div>
      <button onClick={handleLogout}>Deconectează-te</button>
    </div>
    </>
  );
}
