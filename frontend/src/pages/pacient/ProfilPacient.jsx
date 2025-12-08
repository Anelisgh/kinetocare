import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { profileService } from '../../services/profileService';
import PersonalInfo from '../../components/pacient/profil/PersonalInfo';
import TerapeutSection from '../../components/pacient/profil/TerapeutSection';

import '../../styles/profil.css';

export default function ProfilPacient() {
    const { userInfo } = useAuth();
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isEditing, setIsEditing] = useState(false);
    const [locatii, setLocatii] = useState([]);
    const [formData, setFormData] = useState({
        nume: '',
        prenume: '',
        email: '',
        telefon: '',
        dataNasterii: '',
        cnp: '',
        gen: '',
        faceSport: 'NU',
        detaliiSport: '',
        orasPreferat: '',
        locatiePreferataId: '',
    });
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');

    useEffect(() => {
        fetchData();
    }, []);
    // Fetch profile and locatii data
    const fetchData = async () => {
        try {
            setLoading(true);
            const [profileData, locatiiData] = await Promise.all([
                profileService.getProfile(),
                profileService.getLocatii()
            ]);

            setProfile(profileData);
            setLocatii(locatiiData);

            const formattedDate = profileData.dataNasterii
                ? new Date(profileData.dataNasterii).toISOString().split('T')[0] : '';
            setFormData({
                nume: profileData.nume || '',
                prenume: profileData.prenume || '',
                email: profileData.email || '',
                telefon: profileData.telefon || '',
                dataNasterii: formattedDate,
                cnp: profileData.cnp || '',
                gen: profileData.gen || '',
                faceSport: profileData.faceSport || 'NU',
                detaliiSport: profileData.detaliiSport || '',
                orasPreferat: profileData.orasPreferat || '',
                locatiePreferataId: profileData.locatiePreferataId ? String(profileData.locatiePreferataId) : '',
            });
        } catch (error) {
            console.error('Eroare la încărcarea profilului:', error);
            setError('Nu s-a putut încărca profilul. Încearcă din nou.');
        } finally {
            setLoading(false);
        }
    };
    // Handle form field changes
    const handleChange = (e) => {
        const { name, value } = e.target;

        const newValue = name === 'locatiePreferataId' ? value : value;

        if (name === 'faceSport' && value === 'NU') {
            setFormData(prev => ({
                ...prev,
                [name]: value,
                detaliiSport: '',
            }));
        } else {
            setFormData(prev => ({
                ...prev,
                [name]: newValue
            }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccessMessage('');

        if (formData.cnp && !/^\d{13}$/.test(formData.cnp)) {
            setError('CNP-ul trebuie să conțină exact 13 cifre.');
            return;
        }

        const dataToSend = {};
        const profileData = {
            ...profile,
            dataNasterii: profile.dataNasterii
                ? new Date(profile.dataNasterii).toISOString().split('T')[0]
                : '',
            orasPreferat: profile.orasPreferat || '',
            locatiePreferataId: profile.locatiePreferataId ? String(profile.locatiePreferataId) : '',
        };

        let hasChanges = false;
        Object.keys(formData).forEach(key => {
            if (formData[key] !== profileData[key]) {
                if (key === 'locatiePreferataId') {
                    dataToSend[key] = formData[key] === '' ? null : Number(formData[key]);
                } else {
                    dataToSend[key] = formData[key];
                }
                hasChanges = true;
            }
        });

        if (dataToSend.faceSport === 'NU') {
            dataToSend.detaliiSport = null;
            hasChanges = true;
        }

        if (!hasChanges) {
            setIsEditing(false);
            setSuccessMessage('Nu a fost detectată nicio modificare.');
            setTimeout(() => setSuccessMessage(''), 3000);
            return;
        }

        try {
            const updatedProfile = await profileService.updateProfile(dataToSend);
            setProfile(updatedProfile);

            const newFormattedDate = updatedProfile.dataNasterii
                ? new Date(updatedProfile.dataNasterii).toISOString().split('T')[0]
                : '';

            setFormData({
                ...formData,
                dataNasterii: newFormattedDate
            });

            setIsEditing(false);
            setSuccessMessage('Profil actualizat cu succes!');
            setTimeout(() => setSuccessMessage(''), 3000);
        } catch (error) {
            console.error('Eroare la actualizare:', error);
            setError('Nu s-a putut actualiza profilul. Verifică datele și încearcă din nou.');
        }
    };

    const handleCancel = () => {
        const formattedDate = profile.dataNasterii
            ? new Date(profile.dataNasterii).toISOString().split('T')[0]
            : '';

        setFormData({
            nume: profile.nume || '',
            prenume: profile.prenume || '',
            email: profile.email || '',
            telefon: profile.telefon || '',
            dataNasterii: formattedDate,
            cnp: profile.cnp || '',
            gen: profile.gen || '',
            faceSport: profile.faceSport || 'NU',
            detaliiSport: profile.detaliiSport || '',
            orasPreferat: profile.orasPreferat || '',
            locatiePreferataId: profile.locatiePreferataId ? String(profile.locatiePreferataId) : '',
        });
        setIsEditing(false);
        setError(null);
    };

    if (loading) {
        return (
            <div className="profil-container">
                <div className="loading-spinner">Se încarcă...</div>
            </div>
        );
    }

    if (!profile) {
        return (
            <div className="profil-container">
                <div className="error-message">Nu s-a putut încărca profilul.</div>
            </div>
        );
    }

    return (
        <div className="profil-container">
            <div className="profil-header">
                <h1>Profil</h1>
                {!isEditing && (
                    <button
                        className="btn-edit"
                        onClick={() => setIsEditing(true)}
                    >
                        Editează
                    </button>
                )}
            </div>

            {successMessage && (
                <div className="success-message">{successMessage}</div>
            )}

            {error && (
                <div className="error-message">{error}</div>
            )}

            {isEditing ? (
                <form onSubmit={handleSubmit} className="profil-form">
                    <PersonalInfo
                        profile={profile}
                        formData={formData}
                        isEditing={true}
                        onChange={handleChange}
                        locatii={locatii}
                        assignedTerapeut={profile.terapeutDetalii}
                    />

                    <div className="form-actions">
                        <button type="submit" className="btn-save">
                            Salvează
                        </button>
                        <button type="button" className="btn-cancel" onClick={handleCancel}>
                            Anulează
                        </button>
                    </div>
                </form>
            ) : (
                <div className="profil-info">
                    <PersonalInfo
                        profile={profile}
                        formData={formData}
                        isEditing={false}
                        onChange={handleChange}
                        locatii={locatii}
                        assignedTerapeut={profile.terapeutDetalii}
                    />
                    <div id='choose-terapeut'
                        style={{ scrollMarginTop: '100px' }}>
                        <TerapeutSection
                            dataNasterii={profile.dataNasterii}
                            onProfileUpdate={fetchData} />
                    </div>
                </div>
            )}
        </div>
    );
}