import React, { useState, useEffect, useMemo } from 'react';
import { profileService } from '../../../services/profileService';
import '../../../styles/terapeutSearch.css';

// Formular de cautare terapeut
export default function TerapeutSearchForm({ specializare, onSearch, loading }) {
    const [selections, setSelections] = useState({
        judet: '',
        oras: '',
        locatieId: '',
        gen: ''
    });
    
    const [allLocatii, setAllLocatii] = useState([]);
    const [loadingLocatii, setLoadingLocatii] = useState(true);
    const [error, setError] = useState(null);

    // fetch toate locatiile la montarea componentei
    useEffect(() => {
        const fetchLocatii = async () => {
            try {
                const data = await profileService.getLocatii();
                setAllLocatii(data);
            } catch (err) {
                console.error('Error fetching locatii:', err);
                setError('Nu s-au putut încărca locațiile.');
            } finally {
                setLoadingLocatii(false);
            }
        };
        fetchLocatii();
    }, []);

    // calculam judetele pentru dropdown-uri
    const judeteOptions = useMemo(() => {
        if (!allLocatii.length) return [];
        // eliminam duplicatele si sortam alfabetic
        return [...new Set(allLocatii.map(loc => loc.judet))].sort();
    }, [allLocatii]);

    // calculam orasele (bazate pe judetul selectat)
    const orasOptions = useMemo(() => {
        if (!selections.judet) return [];
        const filtered = allLocatii.filter(loc => loc.judet === selections.judet);
        return [...new Set(filtered.map(loc => loc.oras))].sort();
    }, [allLocatii, selections.judet]);

    // calculam locatiile (bazate pe orasul selectat)
    const locatiiOptions = useMemo(() => {
        if (!selections.oras) return [];
        return allLocatii
            .filter(loc => loc.oras === selections.oras)
            .sort((a, b) => a.nume.localeCompare(b.nume));
    }, [allLocatii, selections.oras]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        
        setSelections(prev => {
            const next = { ...prev, [name]: value };
            // resetam campurile dependente daca e cazul
            if (name === 'judet') {
                next.oras = '';
                next.locatieId = '';
            }
            if (name === 'oras') {
                next.locatieId = '';
            }
            return next;
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        // trimiterea datelor de cautare catre parinte
        onSearch({
            specializare,
            judet: selections.judet,
            oras: selections.oras,
            locatieId: selections.locatieId,
            gen: selections.gen
        });
    };

    const handleReset = () => {
        setSelections({ judet: '', oras: '', locatieId: '', gen: '' });
    };

    if (loadingLocatii) return <div className="loading-state">Se încarcă filtrele...</div>;
    if (error) return <div className="error-message">{error}</div>;

    return (
        <form onSubmit={handleSubmit} className="terapeut-search-form">
            <p className="specializare-info">
                Caută terapeut pentru: <strong>{specializare === 'PEDIATRIE' ? 'Copii' : 'Adulți'}</strong>
            </p>

            <div className="search-filters">
                <div className="form-group">
                    <label htmlFor="judet">1. Județ</label>
                    <select 
                        id="judet"
                        name="judet" 
                        value={selections.judet} 
                        onChange={handleChange}
                    >
                        <option value="">Selectează Județul</option>
                        {judeteOptions.map(j => <option key={j} value={j}>{j}</option>)}
                    </select>
                </div>

                <div className="form-group">
                    <label htmlFor="oras">2. Oraș</label>
                    <select 
                        id="oras"
                        name="oras" 
                        value={selections.oras} 
                        onChange={handleChange}
                        disabled={!selections.judet}
                    >
                        <option value="">Selectează Orașul</option>
                        {orasOptions.map(o => <option key={o} value={o}>{o}</option>)}
                    </select>
                </div>

                <div className="form-group">
                    <label htmlFor="locatieId">3. Locație (Clinica/Cabinet)</label>
                    <select 
                        id="locatieId"
                        name="locatieId" 
                        value={selections.locatieId} 
                        onChange={handleChange}
                        disabled={!selections.oras}
                    >
                        <option value="">Selectează Locația</option>
                        {locatiiOptions.map(loc => (
                            <option key={loc.id} value={loc.id}>{loc.nume}</option>
                        ))}
                    </select>
                </div>

                <div className="form-group">
                    <label htmlFor="gen">4. Preferință Gen</label>
                    <select 
                        id="gen"
                        name="gen" 
                        value={selections.gen} 
                        onChange={handleChange}
                    >
                        <option value="">Nu contează</option>
                        <option value="MASCULIN">Masculin</option>
                        <option value="FEMININ">Feminin</option>
                    </select>
                </div>
            </div>

            <div className="search-actions">
                <button 
                    type="submit" 
                    className="btn-search" 
                    disabled={loading || !selections.locatieId} 
                >
                    {loading ? 'Se caută...' : 'Caută Terapeuți'}
                </button>
                <button type="button" className="btn-reset" onClick={handleReset}>
                    Resetează
                </button>
            </div>
        </form>
    );
}