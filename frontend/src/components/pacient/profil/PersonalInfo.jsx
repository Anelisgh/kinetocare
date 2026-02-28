import React, { useMemo, useEffect } from 'react';

// componenta pentru vizualizarea si editarea datelor personale si preferintelor din profil
export default function PersonalInfo({ profile, formData, isEditing, onChange, locatii = [], assignedTerapeut, errors = {} }) {
    
    // lista de locatii disponibile 
    const availableLocatii = useMemo(() => {
        if (assignedTerapeut && assignedTerapeut.locatiiDisponibile && assignedTerapeut.locatiiDisponibile.length > 0) {
             return assignedTerapeut.locatiiDisponibile;
        }
        return locatii;
    }, [locatii, assignedTerapeut]);

    // orasele
    const oraseOptions = useMemo(() => {
        if (!availableLocatii.length) return [];
        return [...new Set(availableLocatii.map(loc => loc.oras))].sort();
    }, [availableLocatii]);

    // locatiile filtrate dupa orasul selectat
    const locatiiOptions = useMemo(() => {
        if (!formData.orasPreferat) return [];
        return availableLocatii
            .filter(loc => loc.oras === formData.orasPreferat)
            .sort((a, b) => a.nume.localeCompare(b.nume));
    }, [availableLocatii, formData.orasPreferat]);

    //resetam id-ul locatiei cand se schimba orasul
    const handleOrasChange = (e) => {
        onChange(e);
        onChange({ target: { name: 'locatiePreferataId', value: '' } });
    };

    // auto-selectam locatia daca e doar una disponibila
    useEffect(() => {
        if (isEditing && assignedTerapeut && availableLocatii.length === 1) {
            const unicaLocatie = availableLocatii[0];
            if (formData.locatiePreferataId !== unicaLocatie.id) {
                onChange({ target: { name: 'orasPreferat', value: unicaLocatie.oras } });
                onChange({ target: { name: 'locatiePreferataId', value: unicaLocatie.id } });
            }
        }
        
        // daca locatia selectata nu mai e valida, o resetam
        if (isEditing && assignedTerapeut && formData.locatiePreferataId) {
             const isValid = availableLocatii.find(l => l.id === Number(formData.locatiePreferataId));
             if (!isValid) {
                 onChange({ target: { name: 'locatiePreferataId', value: '' } });
             }
        }
    }, [isEditing, assignedTerapeut, availableLocatii, formData.locatiePreferataId, onChange]);

    // locatia selectata pentru afisare in modul view
    const locatieSelectata = locatii.find(l => l.id === Number(profile.locatiePreferataId));


    // EDIT MODE
    if (isEditing) {
        return (
            <div className="form-section">
                <h2>Date Personale și Preferințe</h2>

                {/* SECȚIUNEA 1: DATE PERSONALE */}
                <div className="form-group">
                    <label htmlFor="prenume">Prenume *</label>
                    <input 
                        type="text" 
                        id="prenume" 
                        name="prenume" 
                        value={formData.prenume} 
                        onChange={onChange} 
                        required 
                        className={errors.prenume ? 'input-error' : ''}
                    />
                    {errors.prenume && <small className="error-text">{errors.prenume}</small>}
                </div>

                <div className="form-group">
                    <label htmlFor="nume">Nume *</label>
                    <input 
                        type="text" 
                        id="nume" 
                        name="nume" 
                        value={formData.nume} 
                        onChange={onChange} 
                        required 
                        className={errors.nume ? 'input-error' : ''}
                    />
                    {errors.nume && <small className="error-text">{errors.nume}</small>}
                </div>

                <div className="form-group">
                    <label htmlFor="email">Email *</label>
                    <input 
                        type="email" 
                        id="email" 
                        name="email" 
                        value={formData.email} 
                        onChange={onChange} 
                        required 
                        className={errors.email ? 'input-error' : ''}
                    />
                    {errors.email && <small className="error-text">{errors.email}</small>}
                </div>

                <div className="form-group">
                    <label htmlFor="telefon">Telefon *</label>
                    <input 
                        type="tel" 
                        id="telefon" 
                        name="telefon" 
                        value={formData.telefon} 
                        onChange={onChange} 
                        required 
                        pattern="[0-9]{10}" 
                        title="10 cifre" 
                        className={errors.telefon ? 'input-error' : ''}
                    />
                    {errors.telefon && <small className="error-text">{errors.telefon}</small>}
                </div>

                <div className="form-group">
                    <label htmlFor="cnp">CNP *</label>
                    <input 
                        type="text" 
                        id="cnp" 
                        name="cnp" 
                        value={formData.cnp} 
                        onChange={onChange} 
                        required 
                        maxLength="13" 
                        pattern="[0-9]{13}" 
                        className={errors.cnp ? 'input-error' : ''}
                    />
                    {errors.cnp && <small className="error-text">{errors.cnp}</small>}
                </div>

                <div className="form-group">
                    <label htmlFor="dataNasterii">Data Nașterii *</label>
                    <input 
                        type="date" 
                        id="dataNasterii" 
                        name="dataNasterii" 
                        value={formData.dataNasterii} 
                        onChange={onChange} 
                        required 
                        className={errors.dataNasterii ? 'input-error' : ''}
                    />
                    {errors.dataNasterii && <small className="error-text">{errors.dataNasterii}</small>}
                </div>

                <div className="form-group">
                    <label htmlFor="gen">Gen *</label>
                    <select 
                        id="gen" 
                        name="gen" 
                        value={formData.gen} 
                        onChange={onChange} 
                        required
                        className={errors.gen ? 'input-error' : ''}
                    >
                        <option value="">Selectează...</option>
                        <option value="MASCULIN">Masculin</option>
                        <option value="FEMININ">Feminin</option>
                    </select>
                    {errors.gen && <small className="error-text">{errors.gen}</small>}
                </div>

                <div className="form-group">
                    <label htmlFor="faceSport">Practici sport? *</label>
                    <select id="faceSport" name="faceSport" value={formData.faceSport} onChange={onChange} required>
                        <option value="DA">Da</option>
                        <option value="NU">Nu</option>
                    </select>
                </div>

                {formData.faceSport === 'DA' && (
                    <div className="form-group">
                        <label htmlFor="detaliiSport">Descriere sport</label>
                        <textarea id="detaliiSport" name="detaliiSport" value={formData.detaliiSport || ''} onChange={onChange} rows="3" maxLength="500" placeholder="Ex: Fotbal, 3 ori pe săptămână" />
                    </div>
                )}

                {/* SECȚIUNEA 2: LOCATIE */}
                <h3>Locația Preferată</h3>
                <p>
                    {assignedTerapeut 
                        ? `Selectează clinica unde te vei întâlni cu terapeutul ${assignedTerapeut.nume || ''}.`
                        : "Selectează clinica unde dorești să mergi la programări."}
                </p>

                <div className="form-group">
                    <label htmlFor="orasPreferat">Oraș Preferat</label>
                    <select id="orasPreferat" name="orasPreferat" value={formData.orasPreferat} onChange={handleOrasChange}>
                        <option value="">-- Selectează Orașul --</option>
                        {oraseOptions.map(oras => <option key={oras} value={oras}>{oras}</option>)}
                    </select>
                </div>

                <div className="form-group">
                    <label htmlFor="locatiePreferataId">Clinica / Locația</label>
                    <select id="locatiePreferataId" name="locatiePreferataId" value={formData.locatiePreferataId} onChange={onChange} disabled={!formData.orasPreferat}>
                        <option value="">-- Selectează Clinica --</option>
                        {locatiiOptions.map(loc => <option key={loc.id} value={loc.id}>{loc.nume} - {loc.adresa}</option>)}
                    </select>
                </div>

    
            </div>
        );
    }

    // VIEW MODE
    return (
        <div className="info-section">
            <h2>Date Personale</h2>

            <div className="info-item">
                <span className="info-label">Nume Complet: </span>
                <span className="info-value">{profile.nume} {profile.prenume}</span>
            </div>
            <div className="info-item">
                <span className="info-label">CNP: </span>
                <span className="info-value">{profile.cnp || 'N/A'}</span>
            </div>
            <div className="info-item">
                <span className="info-label">Data Nașterii: </span>
                <span className="info-value">
                    {profile.dataNasterii ? new Date(profile.dataNasterii).toLocaleDateString('ro-RO') : 'N/A'}
                </span>
            </div>
            <div className="info-item">
                <span className="info-label">Gen: </span>
                <span className="info-value">
                    {profile.gen === 'MASCULIN' ? 'Masculin' : profile.gen === 'FEMININ' ? 'Feminin' : 'N/A'}
                </span>
            </div>
            
            <h3>Contact</h3>
            <div className="info-item">
                <span className="info-label">Email: </span>
                <span className="info-value">{profile.email}</span>
            </div>
            <div className="info-item">
                <span className="info-label">Telefon: </span>
                <span className="info-value">{profile.telefon}</span>
            </div>

            <h3>Preferințe</h3>
            <div className="info-item">
                <span className="info-label">Locație preferată: </span>
                <span className="info-value">
                    {locatieSelectata ? `${locatieSelectata.nume} (${locatieSelectata.oras})` : 'Neselectată'}
                </span>
            </div>
            <div className="info-item">
                <span className="info-label">Practici sport: </span>
                <span className="info-value">{profile.faceSport === 'DA' ? 'Da' : 'Nu'}</span>
            </div>
            {profile.faceSport === 'DA' && profile.detaliiSport && (
                <div className="info-item">
                    <span className="info-label">Detalii sport: </span>
                    <span className="info-value">{profile.detaliiSport}</span>
                </div>
            )}
        </div>
    );
}