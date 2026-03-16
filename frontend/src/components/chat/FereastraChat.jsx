import React, { useState, useEffect, useRef } from 'react';
import chatService from '../../services/chatService';
import { authService } from '../../services/authService';
import '../../styles/chat.css';

const FereastraChat = ({ conversatieActiva, userId, tipUser, stompClient, onMesajNouDupaCitire, arhivatiIds = [] }) => {
  const [istoric, setIstoric] = useState([]);
  const [mesajInput, setMesajInput] = useState('');
  const [eroareWs, setEroareWs] = useState(null); // eroare primită de la backend via WebSocket
  const mesajeEndRef = useRef(null);

  // Funcție de scroll bottom
  const executaScrollJos = () => {
    mesajeEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const conversatieId = conversatieActiva?.id;

  useEffect(() => {
    let sub = null;
    let subErori = null;

    if (conversatieId && stompClient && stompClient.connected) {
      // 1. Încarcă istoricul de mesaje REST
      chatService.obtineMesajeDinConversatie(conversatieId)
        .then(mesaje => {
          setIstoric(mesaje);
          executaScrollJos();
        })
        .catch(err => console.error("Eroare la încărcarea istoricului de mesaje", err));
      
      // 2. Marchează mesajele curente ca fiind citite
      chatService.marcheazaMesajeleCaCitite(conversatieId, userId, tipUser)
         .then(() => {
             if (onMesajNouDupaCitire) onMesajNouDupaCitire();
         });

      // 3. Subscrie-te la topicul specific conversației pentru STOMP
      const subscriptionPath = `/queue/conversatii/${conversatieId}`;
      sub = stompClient.subscribe(subscriptionPath, (message) => {
         const mesajPrimit = JSON.parse(message.body);
         setIstoric(prev => [...prev, mesajPrimit]);
         executaScrollJos();
         
         // Dacă primesc un mesaj pe conv activă, îl marchez ca citit direct în fundal
         if (mesajPrimit.expeditorKeycloakId !== userId) {
             chatService.marcheazaMesajeleCaCitite(conversatieId, userId, tipUser)
               .then(() => { if (onMesajNouDupaCitire) onMesajNouDupaCitire(); });
         }
      });

      // 4. Subscrie-te la /user/queue/errors pentru a primi erorile de la @MessageExceptionHandler
      // (ex: validare eșuată, eroare DB la salvarea mesajului)
      subErori = stompClient.subscribe('/user/queue/errors', (frame) => {
        const eroare = JSON.parse(frame.body);
        const mesajEroare = eroare?.error || 'Mesajul nu a putut fi trimis.';
        setEroareWs(mesajEroare);
        // auto-dismiss după 5 secunde
        setTimeout(() => setEroareWs(null), 5000);
      });
    }

    return () => {
       if (sub) sub.unsubscribe();
       if (subErori) subErori.unsubscribe();
    };
  }, [conversatieId, stompClient, userId, tipUser]);

  
  useEffect(() => {
     executaScrollJos();
  }, [istoric]);


  const trimiteMesaj = (e) => {
    e.preventDefault();
    if (!mesajInput.trim() || !stompClient || !stompClient.connected || !conversatieActiva) return;

    const partnerId = tipUser === 'PACIENT' ? conversatieActiva.terapeutKeycloakId : conversatieActiva.pacientKeycloakId;

    const payload = {
      conversatieId: conversatieActiva.id || null,
      expeditorKeycloakId: userId,
      destinatarKeycloakId: partnerId,
      tipExpeditor: tipUser,
      continut: mesajInput.trim()
    };

    // Trimite mesaj folosind Clientul STOMP pe ruta @MessageMapping("/chat.send")
    stompClient.publish({
      destination: '/app/chat.send',
      headers: {
        Authorization: `Bearer ${authService.getToken()}`
      },
      body: JSON.stringify(payload)
    });

    setMesajInput('');
  };

  if (!conversatieActiva) {
    return (
      <div className="fereastra-goala">
        <div className="fereastra-goala-icon">💬</div>
        <h2>Nicio conversație selectată</h2>
        <p>Alege o conversație din panoul din stânga pentru a începe comunicarea.</p>
      </div>
    );
  }

  const formateazaData = (dataIso) => {
    if (!dataIso) return '';
    const date = new Date(dataIso);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className="fereastra-chat">
      <div className="chat-header">
        <h3>{conversatieActiva.numeDisplay || 'Conversație'}</h3>
      </div>

      {/* Banner eroare WebSocket — apare când backend-ul trimite o eroare via @MessageExceptionHandler */}
      {eroareWs && (
        <div style={{
          backgroundColor: '#fff5f5',
          borderLeft: '4px solid #e53e3e',
          color: '#c53030',
          padding: '0.6rem 1rem',
          fontSize: '0.875rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          gap: '0.5rem',
        }}>
          <span>⚠️ {eroareWs}</span>
          <button
            onClick={() => setEroareWs(null)}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#c53030', fontWeight: 'bold', fontSize: '1rem' }}
            aria-label="Închide eroarea"
          >✕</button>
        </div>
      )}
      <div className="chat-body">
        {istoric.map(mesaj => {
          const isMine = mesaj.expeditorKeycloakId === userId;
          return (
            <div key={mesaj.id} className={`mesaj-wrapper ${isMine ? 'mine' : 'theirs'}`}>
               <div className="mesaj-bula">
                 <p>{mesaj.continut}</p>
                 <span className="mesaj-timp">{formateazaData(mesaj.trimisLa)}</span>
               </div>
            </div>
          );
        })}
        <div ref={mesajeEndRef} />
      </div>

      <div className="chat-footer">
        {(() => {
           const partnerId = tipUser === 'PACIENT' ? conversatieActiva.terapeutKeycloakId : conversatieActiva.pacientKeycloakId;
           const isArchived = arhivatiIds.includes(partnerId);

           if (isArchived) {
             return (
                <div className="arhivat-banner">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="w-5 h-5">
                    <path fillRule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25zM12.75 9a.75.75 0 00-1.5 0v2.246l-1.428 1.428a.75.75 0 101.06 1.06l1.868-1.868v-2.866z" clipRule="evenodd" />
                  </svg>
                  <span>Această conversație este arhivată și poate fi doar citită (Relație terapeutică inactivă).</span>
                </div>
             );
           }

           return (
              <form onSubmit={trimiteMesaj}>
                <div className="chat-input-wrapper">
                   <input 
                      type="text" 
                      placeholder="Scrie un mesaj aici..." 
                      value={mesajInput}
                      onChange={(e) => setMesajInput(e.target.value)}
                   />
                </div>
                <button type="submit" disabled={!mesajInput.trim()} title="Trimite">
                   <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M3.478 2.404a.75.75 0 00-.926.941l2.432 7.905H13.5a.75.75 0 010 1.5H4.984l-2.432 7.905a.75.75 0 00.926.94 60.519 60.519 0 0018.445-8.986.75.75 0 000-1.218A60.517 60.517 0 003.478 2.404z" />
                   </svg>
                </button>
              </form>
           );
        })()}
      </div>
    </div>
  );
};

export default FereastraChat;
