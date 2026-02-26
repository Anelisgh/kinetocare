import React from 'react';
import '../../styles/chat.css'; // O să creăm niște stiluri simple

const ListaConversatii = ({ conversatii, conversatieActivaId, peSelectieConversatie, userId, tipUser, numeMap = {}, arhivatiIds = [] }) => {

  const obtineNumePartener = (conversatie) => {
      const partenerId = tipUser === 'PACIENT' ? conversatie.terapeutId : conversatie.pacientId;
      if (numeMap[partenerId]) {
          return numeMap[partenerId];
      }
      return tipUser === 'PACIENT' ? `Terapeut #${partenerId}` : `Pacient #${partenerId}`; 
  };

  const formateazaData = (dataIso) => {
    if (!dataIso) return '';
    const date = new Date(dataIso);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  const initialaPartener = String(obtineNumePartener({})).charAt(0).toUpperCase(); // Helper placeholder until we fetch names

  const isArhivat = (conversatie) => {
      const partenerId = tipUser === 'PACIENT' ? conversatie.terapeutId : conversatie.pacientId;
      return arhivatiIds.includes(partenerId);
  };

  const conversatiiSortate = [...conversatii].sort((a, b) => {
      const aArhivat = isArhivat(a);
      const bArhivat = isArhivat(b);
      if (aArhivat && !bArhivat) return 1;
      if (!aArhivat && bArhivat) return -1;
      // Daca au acelasi status, raman sortate default (de la backend) de ex. dupa data ultimului mesaj
      return 0;
  });

  return (
    <div className="lista-conversatii">
      <div className="lista-header">
        <h3>Conversații</h3>
      </div>
      <div className="lista-body">
        {conversatiiSortate.length === 0 ? (
          <p className="no-conversatii">Nicio conversație recentă.</p>
        ) : (
          conversatiiSortate.map(conv => {
            const isActive = conv.id === conversatieActivaId;
            const ultimulMesaj = conv.ultimulMesaj;
            // Presupunem că e necitit dacă expeditorul nu e user-ul curent și esteCitit e false
            const isNecitit = ultimulMesaj && !ultimulMesaj.esteCitit && ultimulMesaj.expeditorId !== userId;

            return (
              <div 
                key={conv.id} 
                className={`conversatie-item ${isActive ? 'active' : ''} ${isNecitit ? 'necitit' : ''} ${isArhivat(conv) ? 'arhivat' : ''}`}
                onClick={() => peSelectieConversatie(conv)}
              >
                <div className="conv-avatar">
                   <div className="avatar-placeholder">{obtineNumePartener(conv).charAt(0).toUpperCase() || 'P'}</div>
                </div>
                <div className="conv-detalii">
                  <div className="conv-top">
                    <h4 className="conv-nume">
                      {obtineNumePartener(conv)}
                      {isArhivat(conv) && <span className="badge-arhivat">Arhivat</span>}
                    </h4>
                    {ultimulMesaj && <span className="conv-timp">{formateazaData(ultimulMesaj.trimisLa)}</span>}
                  </div>
                  <div className="conv-bottom">
                    <p className="conv-prevVizualizare">
                      {ultimulMesaj ? ultimulMesaj.continut : 'Niciun mesaj.'}
                    </p>
                    {isNecitit && <span className="badge-necitit"></span>}
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};

export default ListaConversatii;
