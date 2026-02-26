import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { notificariService } from '../services/notificariService';
import '../styles/notificari.css';

// maparea urlActiune din backend -> rute frontend
const mapUrlActiune = (urlActiune, tipUser) => {
  if (!urlActiune) return null;

  const prefix = tipUser === 'TERAPEUT' ? '/terapeut' : '/pacient';

  // /calendar -> /terapeut/homepage
  if (urlActiune === '/calendar') return `${prefix}/homepage`;
  // /pacienti -> /terapeut/pacienti
  if (urlActiune === '/pacienti') return `${prefix}/pacienti`;
  // /fisa-pacient/5 -> /terapeut/pacienti/5
  if (urlActiune.startsWith('/fisa-pacient/')) {
    const pacientId = urlActiune.replace('/fisa-pacient/', '');
    return `/terapeut/pacienti/${pacientId}`;
  }
  // /programari -> /pacient/programari
  if (urlActiune === '/programari') return `${prefix}/programari`;
  // /jurnal -> /pacient/jurnal
  if (urlActiune === '/jurnal') return `${prefix}/jurnal`;
  // /chat/123 -> /chat-terapeut sau /chat-pacient
  if (urlActiune.startsWith('/chat/')) {
    return tipUser === 'TERAPEUT' ? '/chat-terapeut' : '/chat-pacient';
  }

  return `${prefix}/homepage`;
};

// formateaza timpul relativ: "acum 2 min", "acum 3 ore", "acum 2 zile"
const formatTimpRelativ = (dateStr) => {
  const acum = new Date();
  const data = new Date(dateStr);
  const diffMs = acum - data;
  const diffMin = Math.floor(diffMs / 60000);
  const diffOre = Math.floor(diffMs / 3600000);
  const diffZile = Math.floor(diffMs / 86400000);

  if (diffMin < 1) return 'Chiar acum';
  if (diffMin < 60) return `Acum ${diffMin} min`;
  if (diffOre < 24) return `Acum ${diffOre} ${diffOre === 1 ? 'oră' : 'ore'}`;
  if (diffZile < 7) return `Acum ${diffZile} ${diffZile === 1 ? 'zi' : 'zile'}`;
  return data.toLocaleDateString('ro-RO', { day: 'numeric', month: 'short' });
};

// returneaza o iconita in functie de tipul notificarii
const getIcon = (tipNotificare) => {
  switch (tipNotificare) {
    case 'PROGRAMARE_NOUA': return '📅';
    case 'EVALUARE_INITIALA_NOUA': return '📋';
    case 'PROGRAMARE_ANULATA_DE_PACIENT': 
    case 'PROGRAMARE_ANULATA_DE_TERAPEUT': return '❌';
    case 'REEVALUARE_NECESARA':
    case 'REEVALUARE_RECOMANDATA': return '🔄';
    case 'JURNAL_COMPLETAT': return '📔';
    case 'REMINDER_24H':
    case 'REMINDER_2H':
    case 'REMINDER_JURNAL': return '⏰';
    default: return '🔔';
  }
};

export default function NotificationDropdown({ notificari, userKeycloakId, tipUser, onClose, onRefresh }) {
  const navigate = useNavigate();
  const dropdownRef = useRef(null);

  const areNecitite = notificari.some(n => !n.esteCitita);

  // marcheaza toate ca citite
  const handleMarkAllRead = async () => {
    try {
      await notificariService.marcheazaToateCitite(userKeycloakId, tipUser);
      onRefresh();
    } catch (err) {
      console.error('Eroare la marcarea notificărilor:', err);
    }
  };

  // inchidem dropdown la click in afara
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        // verificam daca click-ul e pe bell icon (parintele gestioneaza asta)
        if (!e.target.closest('.notification-bell-wrapper')) {
          onClose();
        }
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [onClose]);

  const handleClick = async (notificare) => {
    try {
      // marcam ca citita
      if (!notificare.esteCitita) {
        await notificariService.marcheazaCitita(notificare.id);
      }
      onClose();
      onRefresh();

      // navigam la pagina relevanta
      const url = mapUrlActiune(notificare.urlActiune, tipUser);
      if (url) navigate(url);
    } catch (err) {
      console.error('Eroare la procesarea notificării:', err);
    }
  };

  return (
    <div className="notification-dropdown" ref={dropdownRef}>
      <div className="notification-dropdown-header">
        <h4>Notificări</h4>
        {areNecitite && (
          <button className="notification-mark-all-btn" onClick={handleMarkAllRead}>
            Marchează toate ca citite
          </button>
        )}
      </div>

      <div className="notification-dropdown-list">
        {notificari.length === 0 ? (
          <div className="notification-empty">
            <span className="notification-empty-icon">🔔</span>
            <p>Nu ai notificări</p>
          </div>
        ) : (
          notificari.map((notif) => (
            <div
              key={notif.id}
              className={`notification-item ${!notif.esteCitita ? 'necitita' : ''}`}
              onClick={() => handleClick(notif)}
            >
                <div className="flex-shrink-0 text-xl mr-3">
                  {getIcon(notif.tipNotificare)}
                </div>
              <div className="notification-item-content">
                <p className="notification-item-titlu">{notif.titlu}</p>
                <p className="notification-item-mesaj">{notif.mesaj}</p>
                <span className="notification-item-timp">
                  {formatTimpRelativ(notif.createdAt)}
                </span>
              </div>
              {!notif.esteCitita && <span className="notification-unread-dot" />}
            </div>
          ))
        )}
      </div>
    </div>
  );
}
