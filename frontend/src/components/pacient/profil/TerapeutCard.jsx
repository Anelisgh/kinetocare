import "../../../styles/terapeutCard.css";

// Cardul terapeutului -> afiseaza detalii despre terapeut
export default function TerapeutCard({ 
    terapeut, 
    isSelected, 
    isChosen, 
    onSelect, 
    showDetails = false 
}) {
    const defaultAvatar = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200'%3E%3Crect fill='%23e0e0e0' width='200' height='200'/%3E%3Ctext fill='%23999' font-family='sans-serif' font-size='80' text-anchor='middle' x='100' y='130'%3E👤%3C/text%3E%3C/svg%3E";

    // functie helper pentru formatarea programului
    const renderProgram = () => {
        if (!terapeut.disponibilitati || terapeut.disponibilitati.length === 0) return null;

        // grupam disponibilitatile pe locatii
        const programPerLocatie = {};
        terapeut.disponibilitati.forEach(disp => {
            const locName = disp.locatieNume || "Locație necunoscută";
            if (!programPerLocatie[locName]) programPerLocatie[locName] = [];
            programPerLocatie[locName].push(disp);
        });

        return (
            <div className="terapeut-schedule">
                <h4 className="schedule-header">📅 Program Disponibil:</h4>
                {Object.entries(programPerLocatie).map(([locatie, orar]) => (
                    <div key={locatie} className="schedule-item">
                        <strong className="schedule-location">{locatie}</strong>
                        <ul className="schedule-times">
                            {orar.sort((a,b) => a.ziSaptamana - b.ziSaptamana).map(slot => (
                                <li key={slot.id}>
                                    {slot.ziSaptamanaNume}: {slot.oraInceput.substring(0,5)} - {slot.oraSfarsit.substring(0,5)}
                                </li>
                            ))}
                        </ul>
                    </div>
                ))}
            </div>
        );
    };

    return (
        <div 
            className={`terapeut-card ${isSelected ? 'selected' : ''} ${isChosen ? 'chosen' : ''}`}
            onClick={onSelect}
        >
            <div className="terapeut-card-header">
                <img 
                    src={terapeut.pozaProfil || defaultAvatar} 
                    alt={`${terapeut.prenume} ${terapeut.nume}`}
                    className="terapeut-avatar"
                />
                <div className="terapeut-info">
                    <h3>{terapeut.prenume} {terapeut.nume}</h3>
                    {showDetails && (
                        <>
                            <p className="terapeut-contact">
                                <span>📧 {terapeut.email}</span>
                            </p>
                            <p className="terapeut-contact">
                                <span>📞 {terapeut.telefon}</span>
                            </p>
                            {renderProgram()}
                        </>
                    )}
                </div>
            </div>

            {terapeut.locatiiDisponibile && terapeut.locatiiDisponibile.length > 0 && (
                <div className="terapeut-locatii">
                    <h4>Locații disponibile:</h4>
                    <ul>
                        {terapeut.locatiiDisponibile.map((locatie) => (
                            <li key={locatie.id}>
                                <strong>{locatie.nume}</strong>
                                <br />
                                <small>{locatie.adresa}, {locatie.oras}, {locatie.judet}</small>
                            </li>
                        ))}
                    </ul>
                </div>
            )}

            {isSelected && !isChosen && (
                <div className="terapeut-selected-indicator">
                    ✓ Selectat
                </div>
            )}
        </div>
    );
}