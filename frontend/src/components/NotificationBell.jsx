import React, { useState, useEffect, useCallback, useRef } from 'react';
import { useAuth } from '../context/AuthContext';
import { profileService } from '../services/profileService';
import { notificariService } from '../services/notificariService';
import NotificationDropdown from './NotificationDropdown';
import '../styles/notificari.css';

const POLL_INTERVAL = 30000; // 30 secunde

export default function NotificationBell() {
  const { userInfo } = useAuth();
  const [userKeycloakId, setUserKeycloakId] = useState(null);
  const [tipUser, setTipUser] = useState(null);
  const [countNecitite, setCountNecitite] = useState(0);
  const [notificari, setNotificari] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const intervalRef = useRef(null);

  // determinam userId si tipUser la mount
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const profile = await profileService.getProfile();
        const roles = userInfo?.roles || [];

        if (roles.includes('TERAPEUT')) {
          setTipUser('TERAPEUT');
        } else if (roles.includes('PACIENT')) {
          setTipUser('PACIENT');
        }
        
        // indiferent de rol, setam keycloakId-ul userului curent
        setUserKeycloakId(profile.keycloakId);
      } catch (err) {
        console.error('Eroare la încărcarea profilului pentru notificări:', err);
      }
    };
    fetchProfile();
  }, [userInfo]);

  // fetch count necitite
  const fetchCount = useCallback(async () => {
    if (!userKeycloakId || !tipUser) return;
    try {
      const count = await notificariService.getNumarNecitite(userKeycloakId, tipUser);
      setCountNecitite(count || 0);
    } catch {
      // silently fail - nu blocam UI-ul
    }
  }, [userKeycloakId, tipUser]);

  // fetch lista completa de notificari (cand se deschide dropdown-ul)
  const fetchNotificari = useCallback(async () => {
    if (!userKeycloakId || !tipUser) return;
    try {
      const data = await notificariService.getNotificari(userKeycloakId, tipUser);
      setNotificari(data || []);
      // actualizam si count-ul
      const necitite = (data || []).filter(n => !n.esteCitita).length;
      setCountNecitite(necitite);
    } catch {
      // silently fail
    }
  }, [userKeycloakId, tipUser]);

  // polling pentru count conditionat de vizibilitatea tab-ului
  useEffect(() => {
    if (!userKeycloakId || !tipUser) return;

    fetchCount(); // fetch initial

    const handleVisibilityChange = () => {
      if (document.visibilityState === 'visible') {
        // Tab activ -> refacem fetch imediat si pornim intervalul
        fetchCount();
        if (!intervalRef.current) {
          intervalRef.current = setInterval(fetchCount, POLL_INTERVAL);
        }
      } else {
        // Tab inactiv -> oprim intervalul pt a cruța resursele
        if (intervalRef.current) {
          clearInterval(intervalRef.current);
          intervalRef.current = null;
        }
      }
    };

    // pornim intervalul prima oara daca suntem in focus
    if (document.visibilityState === 'visible') {
      intervalRef.current = setInterval(fetchCount, POLL_INTERVAL);
    }

    // ascultam event-ul de vizibilitate a paginii
    document.addEventListener('visibilitychange', handleVisibilityChange);

    return () => {
      document.removeEventListener('visibilitychange', handleVisibilityChange);
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [userKeycloakId, tipUser, fetchCount]);

  // cand se deschide dropdown-ul, incarcam notificarile
  const toggleDropdown = () => {
    if (!isOpen) {
      fetchNotificari();
    }
    setIsOpen(prev => !prev);
  };

  // callback cand se inchide/refresh dropdown-ul
  const handleRefresh = () => {
    fetchCount();
  };

  return (
    <div className="notification-bell-wrapper">
      <button
        className="notification-bell-btn"
        onClick={toggleDropdown}
        aria-label="Notificări"
      >
        <span className="notification-bell-content">
          🔔 Notificări
          {countNecitite > 0 && (
            <span className="notification-badge">
              {countNecitite > 99 ? '99+' : countNecitite}
            </span>
          )}
        </span>
      </button>

      {isOpen && (
        <NotificationDropdown
          notificari={notificari}
          userKeycloakId={userKeycloakId}
          tipUser={tipUser}
          onClose={() => setIsOpen(false)}
          onRefresh={handleRefresh}
        />
      )}
    </div>
  );
}
