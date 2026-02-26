import React, { useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import ListaConversatii from '../../components/chat/ListaConversatii';
import FereastraChat from '../../components/chat/FereastraChat';
import chatService from '../../services/chatService';
import { authService } from '../../services/authService';
import { profileService } from '../../services/profileService';
import { programariService } from '../../services/programariService';
import '../../styles/Chat.css';

const ChatPacient = () => {
    const [conversatii, setConversatii] = useState([]);
    const [conversatieActivaId, setConversatieActivaId] = useState(null);
    const [stompClient, setStompClient] = useState(null);
    const [userId, setUserId] = useState(null);
    const [numeMap, setNumeMap] = useState({});
    const [arhivatiIds, setArhivatiIds] = useState([]);
    const tipUser = 'PACIENT';

    const incarcaConversatii = async (resolvedUserId) => {
        const idToUse = typeof resolvedUserId === 'number' || typeof resolvedUserId === 'string' ? resolvedUserId : userId;
        if (!idToUse) return;
        try {
            const conversatiiAgregate = await chatService.getConversatiiAgregate(idToUse, tipUser);
            
            // Mapăm formatul agregate către formatul moștenit pentru a susține componentele inferioare
            const conversatiiMapate = conversatiiAgregate.map(c => ({
                id: c.conversatieId,
                pacientKeycloakId: tipUser === 'PACIENT' ? idToUse : c.partenerKeycloakId,
                terapeutKeycloakId: tipUser === 'PACIENT' ? c.partenerKeycloakId : idToUse,
                isArhivat: c.isArhivat,
                numeDisplay: c.partenerNume,
                ultimulMesaj: c.ultimulMesaj
            }));

            setConversatii(conversatiiMapate);
            
            // Extragem liste ajutătoare (arhivatiIds, numeMap) direct din răspunsul unic
            const mapNume = {};
            const listaArhivati = [];

            conversatiiAgregate.forEach(c => {
                mapNume[c.partenerKeycloakId] = c.partenerNume;
                if (c.isArhivat) {
                    listaArhivati.push(c.partenerKeycloakId);
                }
            });

            setNumeMap(mapNume);
            setArhivatiIds(listaArhivati);

        } catch (err) {
            console.error("Eroare fetching conversatii agregate", err);
        }
    };

    useEffect(() => {
        const initializeChat = async () => {
            try {
                const profile = await profileService.getProfile();
                const currentUserId = profile.keycloakId;  // ← uniform: keycloakId pentru toți
                setUserId(currentUserId);
                
                // Un singur apel asincron (fara POST prematur, fara fallback loop)
                await incarcaConversatii(currentUserId);

                // Inițializare client STOMP
                const client = new Client({
                    webSocketFactory: () => new SockJS('http://localhost:8081/api/chat/ws-chat'),
                    connectHeaders: {
                        Authorization: `Bearer ${authService.getToken()}`
                    },
                    reconnectDelay: 5000,
                    heartbeatIncoming: 4000,
                    heartbeatOutgoing: 4000,
                    onConnect: () => {
                        console.log('Conectat la STOMP ca Pacient');
                        setStompClient(client);
                    },
                    onStompError: (frame) => {
                        console.error('Eroare STOMP: ' + frame.headers['message']);
                    }
                });

                client.activate();
                setStompClient(client);

            } catch (err) {
                console.error("Eroare la obținerea profilului pentru chat:", err);
            }
        };

        if (!userId) {
            initializeChat();
        }

        return () => {
            if (stompClient) stompClient.deactivate();
        };
    }, []);

    const handleSelectConversatie = (conv) => {
        setConversatieActivaId(conv.id);
    };

    const conversatieActiva = conversatii.find(c => c.id === conversatieActivaId);

    return (
        <div className="chat-container-layout">
            <ListaConversatii 
                conversatii={conversatii} 
                conversatieActivaId={conversatieActivaId}
                peSelectieConversatie={handleSelectConversatie}
                userId={userId}
                tipUser={tipUser}
                numeMap={numeMap}
                arhivatiIds={arhivatiIds}
            />
            <FereastraChat 
                conversatieActiva={conversatieActiva}
                userId={userId}
                tipUser={tipUser}
                stompClient={stompClient}
                onMesajNouDupaCitire={incarcaConversatii} // reîmprospătează lista (ultimul mesaj preview/citire)
                arhivatiIds={arhivatiIds}
            />
        </div>
    );
};

export default ChatPacient;
