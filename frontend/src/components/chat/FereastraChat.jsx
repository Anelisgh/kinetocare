import React, { useState, useEffect, useRef } from 'react';
import chatService from '../../services/chatService';
import { authService } from '../../services/authService';
import '../../styles/chat.css';

const FereastraChat = ({ conversatieActiva, userId, tipUser, stompClient, onMesajNouDupaCitire, arhivatiIds = [] }) => {
  const [istoric, setIstoric] = useState([]);
  const [mesajInput, setMesajInput] = useState('');
  const [eroareWs, setEroareWs] = useState(null); // eroare primită de la backend via WebSocket
  const mesajeEndRef = useRef(null);
  const ultimaConversatieMarcataRef = useRef(null);

  // Funcție de scroll bottom
  const executaScrollJos = () => {
    mesajeEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const conversatieId = conversatieActiva?.id;

  useEffect(() => {
    // 1. Resetăm istoricul
    setIstoric([]);

    // 2. Încarcă istoricul de mesaje REST doar dacă avem ID
    if (conversatieId) {
      chatService.obtineMesajeDinConversatie(conversatieId)
        .then(mesaje => {
          setIstoric(mesaje);
          executaScrollJos();
        })
        .catch(err => console.error("Eroare la încărcarea istoricului de mesaje", err));
      
      // 3. Marchează mesajele curente ca fiind citite (DOAR O DATĂ per focus pe această conversație)
      if (ultimaConversatieMarcataRef.current !== conversatieId) {
        ultimaConversatieMarcataRef.current = conversatieId;
        chatService.marcheazaMesajeleCaCitite(conversatieId, userId, tipUser)
           .then(() => {
               if (onMesajNouDupaCitire) onMesajNouDupaCitire();
           });
      }
    }

    let sub = null;
    let subErori = null;

    if (stompClient && stompClient.connected) {
      // 4. Subscrie-te la topicul specific conversației (sau topicul null pentru inițializare)
      const subscriptionPath = `/queue/conversatii/${conversatieId || 'null'}`;
      sub = stompClient.subscribe(subscriptionPath, (message) => {
         const mesajPrimit = JSON.parse(message.body);
         setIstoric(prev => [...prev, mesajPrimit]);
         executaScrollJos();
         
         // Trigger refresh în părinte la orice mesaj (mai ales important pentru primul mesaj care dă ID)
         if (onMesajNouDupaCitire) onMesajNouDupaCitire();

         if (mesajPrimit.expeditorKeycloakId !== userId && conversatieId) {
             chatService.marcheazaMesajeleCaCitite(conversatieId, userId, tipUser);
         }
      });

      subErori = stompClient.subscribe('/user/queue/errors', (frame) => {
        const eroare = JSON.parse(frame.body);
        const mesajEroare = eroare?.error || 'Mesajul nu a putut fi trimis.';
        setEroareWs(mesajEroare);
        setTimeout(() => setEroareWs(null), 5000);
      });
    }

    return () => {
       if (sub) sub.unsubscribe();
       if (subErori) subErori.unsubscribe();
    };
  }, [conversatieId, stompClient, stompClient?.connected, userId, tipUser]);

  
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
        {istoric.map((mesaj, index) => {
          const isMine = mesaj.expeditorKeycloakId === userId;
          
          // Logică pentru separator de dată
          let showDateSeparator = false;
          let dateStr = '';
          
          const currentMsgDate = new Date(mesaj.trimisLa);
          if (index === 0) {
            showDateSeparator = true;
          } else {
            const prevMsgDate = new Date(istoric[index - 1].trimisLa);
            if (currentMsgDate.toDateString() !== prevMsgDate.toDateString()) {
              showDateSeparator = true;
            }
          }
          
          if (showDateSeparator) {
            const azi = new Date();
            const ieri = new Date();
            ieri.setDate(azi.getDate() - 1);
            
            if (currentMsgDate.toDateString() === azi.toDateString()) {
              dateStr = 'Astăzi';
            } else if (currentMsgDate.toDateString() === ieri.toDateString()) {
              dateStr = 'Ieri';
            } else {
              dateStr = currentMsgDate.toLocaleDateString('ro-RO', { day: 'numeric', month: 'long' });
            }
          }

          // Logică pentru indicator citit (doar pentru ultimul mesaj trimis de mine)
          const isLastMessage = index === istoric.length - 1;
          const showReadStatus = isMine && isLastMessage;

          return (
            <React.Fragment key={mesaj.id}>
              {showDateSeparator && (
                <div className="separator-data">
                  <span>{dateStr}</span>
                </div>
              )}
              <div className={`mesaj-wrapper ${isMine ? 'mine' : 'theirs'}`}>
                 <div className="mesaj-bula">
                   <p>{mesaj.continut}</p>
                   <span className="mesaj-timp">{formateazaData(mesaj.trimisLa)}</span>
                 </div>
              </div>
              {showReadStatus && (
                <div className="indicator-citit">
                  {mesaj.esteCitit ? (
                    <span className="vazut">
                      ✓ Văzut
                    </span>
                  ) : (
                    <span className="trimis">
                      ✓ Trimis
                    </span>
                  )}
                </div>
              )}
            </React.Fragment>
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
