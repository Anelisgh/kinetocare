import React, { useState, useEffect } from 'react';
import { profileService } from '../../../services/profileService';
import TerapeutSearchForm from './TerapeutSearchForm';
import TerapeutCard from './TerapeutCard';
import '../../../styles/terapeutSection.css';

export default function TerapeutSection({ dataNasterii, onProfileUpdate }) {
    const [myTerapeut, setMyTerapeut] = useState(null);
    const [hasTerapeut, setHasTerapeut] = useState(false);
    const [showSearch, setShowSearch] = useState(false);
    const [searchResults, setSearchResults] = useState([]);
    const [selectedTerapeutId, setSelectedTerapeutId] = useState(null);
    const [loading, setLoading] = useState(false);
    const [searching, setSearching] = useState(false);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');
    const [currentFilters, setCurrentFilters] = useState({});

    // calculare specializare in functie de varsta
    const getSpecializare = () => {
        if (!dataNasterii) return 'ADULTI';
        
        const birthDate = new Date(dataNasterii);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        
        return age < 18 ? 'PEDIATRIE' : 'ADULTI';
    };

    const specializare = getSpecializare();

    useEffect(() => {
        fetchMyTerapeut();
    }, []);

    // functie pentru preluarea terapeutului curent
    const fetchMyTerapeut = async () => {
        try {
            setLoading(true);
            const data = await profileService.getMyTerapeut();
            
            if (data.hasTerapeut) {
                if (data.disponibilitati && data.disponibilitati.length > 0) {
                    const uniqueLocatii = [];
                    const map = new Map();
                    
                    for (const disp of data.disponibilitati) {
                        if (disp.locatie && !map.has(disp.locatie.id)) {
                            map.set(disp.locatie.id, true);
                            uniqueLocatii.push(disp.locatie);
                        }
                    }
                    data.locatiiDisponibile = uniqueLocatii;
                }
                
                setMyTerapeut(data);
                setHasTerapeut(true);
            } else {
                setHasTerapeut(false);
            }
        } catch (error) {
            console.error('Error fetching my terapeut:', error);
            setHasTerapeut(false);
        } finally {
            setLoading(false);
        }
    };

    // functie pentru cautarea terapeutilor
    const handleSearch = async (filters) => {
        try {
            setSearching(true);
            setCurrentFilters(filters);
            setError(null);
            const results = await profileService.searchTerapeuti(filters);
            setSearchResults(results);
            
            if (results.length === 0) {
                setError('Nu s-au găsit terapeuți care să corespundă criteriilor.');
            }
        } catch (error) {
            console.error('Error searching terapeuti:', error);
            setError('Eroare la căutarea terapeuților. Încercați din nou.');
        } finally {
            setSearching(false);
        }
    };
    // functie pentru selectarea unui terapeut din rezultate
    const handleSelectTerapeut = (terapeutKeycloakId) => {
        setSelectedTerapeutId(terapeutKeycloakId);
    };
    // functie pentru confirmarea alegerii terapeutului
    const handleConfirmChoice = async () => {
        if (!selectedTerapeutId) return;

        try {
            setLoading(true);
            setError(null);
            await profileService.chooseTerapeut(selectedTerapeutId, currentFilters.locatieId);
            
            setSuccessMessage('Terapeut ales cu succes!');
            setTimeout(() => setSuccessMessage(''), 3000);
  
            await fetchMyTerapeut();
            if (onProfileUpdate) {
                await onProfileUpdate(); 
            }
            setShowSearch(false);
            setSearchResults([]);
            setSelectedTerapeutId(null);
        } catch (error) {
            console.error('Error choosing terapeut:', error);
            setError('Eroare la alegerea terapeutului. Încercați din nou.');
        } finally {
            setLoading(false);
        }
    };
    // functie pentru stergerea terapeutului curent
    const handleRemoveTerapeut = async () => {
        if (!window.confirm('Sigur doriți să ștergeți terapeutul ales?')) return;

        try {
            setLoading(true);
            setError(null);
            await profileService.removeTerapeut();
            
            setSuccessMessage('Terapeut șters cu succes!');
            setTimeout(() => setSuccessMessage(''), 3000);
            
            setMyTerapeut(null);
            setHasTerapeut(false);
        } catch (error) {
            console.error('Error removing terapeut:', error);
            setError('Eroare la ștergerea terapeutului. Încercați din nou.');
        } finally {
            setLoading(false);
        }
    };
    
    // functie pentru anularea cautarii
    const handleCancelSearch = () => {
        setShowSearch(false);
        setSearchResults([]);
        setSelectedTerapeutId(null);
        setError(null);
    };

    if (loading && !searching) {
        return (
            <div className="info-section">
                <h2>Terapeutul Meu</h2>
                <div className="loading-spinner">Se încarcă...</div>
            </div>
        );
    }

    return (
        <div className="info-section terapeut-section">
            <h2>Terapeutul Meu</h2>

            {successMessage && (
                <div className="success-message">{successMessage}</div>
            )}

            {error && (
                <div className="error-message">{error}</div>
            )}

            {!showSearch && hasTerapeut && myTerapeut && (
                <div className="my-terapeut">
                    <TerapeutCard 
                        terapeut={myTerapeut} 
                        isChosen={true}
                        showDetails={true}
                    />
                    <div className="terapeut-actions">
                        <button 
                            className="btn-change-terapeut"
                            onClick={() => setShowSearch(true)}
                        >
                            Schimbă
                        </button>
                        <button 
                            className="btn-remove-terapeut"
                            onClick={handleRemoveTerapeut}
                        >
                            Șterge
                        </button>
                    </div>
                </div>
            )}

            {!showSearch && !hasTerapeut && (
                <div className="no-terapeut">
                    <p className="info-message">
                        Nu ai ales încă un terapeut. Caută și alege un terapeut pentru a începe.
                    </p>
                    <button 
                        className="btn-search-terapeut"
                        onClick={() => setShowSearch(true)}
                    >
                        Caută Terapeut
                    </button>
                </div>
            )}

            {showSearch && (
                <div className="terapeut-search">
                    <TerapeutSearchForm 
                        specializare={specializare}
                        onSearch={handleSearch}
                        loading={searching}
                    />

                    {searchResults.length > 0 && (
                        <>
                            <div className="search-results">
                                <h3>Terapeuți disponibili ({searchResults.length})</h3>
                                <div className="terapeuti-grid">
                                    {searchResults.map((terapeut) => (
                                        <TerapeutCard
                                            key={terapeut.keycloakId}
                                            terapeut={terapeut}
                                            isSelected={selectedTerapeutId === terapeut.keycloakId}
                                            onSelect={() => handleSelectTerapeut(terapeut.keycloakId)}
                                        />
                                    ))}
                                </div>
                            </div>

                            {selectedTerapeutId && (
                            <div className="confirm-choice">
                                <button 
                                    className="btn-save"
                                    onClick={handleConfirmChoice}
                                    disabled={loading}
                                >
                                    {loading ? 'Se salvează...' : 'Confirmă'}
                                </button>
                                <button 
                                    className="btn-cancel"
                                    onClick={handleCancelSearch}
                                >
                                    Anulează
                                </button>
                            </div>
                        )}
                    </>
                )}
            </div>
        )}
    </div>
);
}