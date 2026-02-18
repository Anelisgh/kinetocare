import React, { useEffect, useState } from 'react';
import { homepageService } from '../../services/homepageService';
import { programariService } from '../../services/programariService';
import BookingWidget from '../../components/pacient/homepage/BookingWidget';
import ActiveAppointmentCard from '../../components/pacient/homepage/ActiveAppointmentCard';
import HistoryAppointmentCard from '../../components/pacient/programari/HistoryAppointmentCard';
import TerapeutSection from '../../components/pacient/profil/TerapeutSection';
import '../../styles/homepagePacient.css';
import '../../styles/programariPacient.css';

const ProgramariPacient = () => {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [istoric, setIstoric] = useState([]);

    // reincarcare, dupa crearea/anularea unei programari
    const refreshDashboard = async () => {
        try {
            setLoading(true);
            const result = await homepageService.getDashboardData();
            setData(result);

            if (result && result.id) {
                const historyData = await programariService.getIstoricPacient(result.id);
                setIstoric(historyData);
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        refreshDashboard();
    }, []);

    const handleCancel = async (programareId) => {
        if (!window.confirm('Sigur dorești să anulezi programarea?')) return;
        try {
            await programariService.cancelProgramare(programareId);
            refreshDashboard();
        } catch (err) {
            alert(err.message);
        }
    };

    if (loading) return <div className="homepage-loading">Se încarcă programările...</div>;
    if (error) return <div className="homepage-error">Eroare: {error}</div>;
    if (!data) return null;

    const { terapeutDetalii, locatieDetalii, urmatoareaProgramare, dataNasterii } = data;

    return (
        <div className="programari-container">
            <h1 className="programari-title">Programările Mele</h1>

            <div className="programari-layout">

                {/* SECTION 1: Active Appointment / Booking / Therapist*/}
                <div className="programari-active-section">
                    <h2 className="programari-section-title">
                        Activitate Curentă
                    </h2>
                    
                    {/* SCENARIO 1: No Therapist -> Show Search/Select Therapist Component */}
                    {!terapeutDetalii && (
                        <TerapeutSection 
                            dataNasterii={dataNasterii}
                            onProfileUpdate={refreshDashboard}
                        />
                    )}

                    {/* SCENARIO 2: Has Therapist -> Show Logic for Appointments */}
                    {terapeutDetalii && (
                        <>
                             {/* Case A: Active Appointment -> Show It */}
                            {urmatoareaProgramare && (
                                <ActiveAppointmentCard
                                    programare={urmatoareaProgramare}
                                    onCancel={handleCancel}
                                />
                            )}

                            {/* Case B: No Active Appointment -> Show Booking Widget */}
                            {!urmatoareaProgramare && (
                                <BookingWidget
                                    pacientId={data.id}
                                    terapeutId={terapeutDetalii.id}
                                    locatieId={locatieDetalii?.id}
                                    onSuccess={refreshDashboard}
                                />
                            )}
                        </>
                    )}
                </div>


                {/* SECTION 2: History */}
                <div className="programari-history-section">
                    <h2 className="programari-section-title">
                        Istoric Programări
                    </h2>
                    
                    {istoric.length === 0 ? (
                        <div className="programari-empty-history">
                            Nu există istoric de programări.
                        </div>
                    ) : (
                        istoric.map(prog => (
                            <HistoryAppointmentCard key={prog.id} programare={prog} />
                        ))
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProgramariPacient;

