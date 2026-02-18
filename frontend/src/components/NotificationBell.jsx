import React, { useState, useEffect, useCallback, useRef } from 'react';
import { useAuth } from '../context/AuthContext';
import { profileService } from '../services/profileService';
import { notificariService } from '../services/notificariService';
import NotificationDropdown from './NotificationDropdown';
import '../styles/notificari.css';

const POLL_INTERVAL = 30000; // 30 secunde

export default function NotificationBell() {
  const { userInfo } = useAuth();
  const [userId, setUserId] = useState(null);
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
          setUserId(profile.terapeutId || profile.id);
          setTipUser('TERAPEUT');
        } else if (roles.includes('PACIENT')) {
          setUserId(profile.id);
          setTipUser('PACIENT');
        }
      } catch (err) {
        console.error('Eroare la încărcarea profilului pentru notificări:', err);
      }
    };
    fetchProfile();
  }, [userInfo]);

  // fetch count necitite
  const fetchCount = useCallback(async () => {
    if (!userId || !tipUser) return;
    try {
      const count = await notificariService.getNumarNecitite(userId, tipUser);
      setCountNecitite(count || 0);
    } catch {
      // silently fail - nu blocam UI-ul
    }
  }, [userId, tipUser]);

  // fetch lista completa de notificari (cand se deschide dropdown-ul)
  const fetchNotificari = useCallback(async () => {
    if (!userId || !tipUser) return;
    try {
      const data = await notificariService.getNotificari(userId, tipUser);
      setNotificari(data || []);
      // actualizam si count-ul
      const necitite = (data || []).filter(n => !n.esteCitita).length;
      setCountNecitite(necitite);
    } catch {
      // silently fail
    }
  }, [userId, tipUser]);

  // polling pentru count
  useEffect(() => {
    if (!userId || !tipUser) return;

    fetchCount(); // fetch initial
    intervalRef.current = setInterval(fetchCount, POLL_INTERVAL);

    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [userId, tipUser, fetchCount]);

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
        <svg
          className="notification-bell-icon"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
          <path d="M13.73 21a2 2 0 0 1-3.46 0" />
        </svg>

        {countNecitite > 0 && (
          <span className="notification-badge">
            {countNecitite > 99 ? '99+' : countNecitite}
          </span>
        )}
      </button>

      {isOpen && (
        <NotificationDropdown
          notificari={notificari}
          userId={userId}
          tipUser={tipUser}
          onClose={() => setIsOpen(false)}
          onRefresh={handleRefresh}
        />
      )}
    </div>
  );
}
