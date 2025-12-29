import { useEffect, useState } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { profileService } from '../services/profileService';

export default function ProfileGuard() {
  const [loading, setLoading] = useState(true);
  const [profileComplete, setProfileComplete] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    checkProfile();
  }, []);

  const checkProfile = async () => {
    try {
      const profile = await profileService.getProfile();
      // flag-ul din backend spune ca e incomplet
      if (profile.profileIncomplete === true) {
        setProfileComplete(false);
      }
      // daca flag-ul lipseste verificam manual campurile
      else if (profile.profileIncomplete == null && (!profile.cnp || !profile.dataNasterii)) {
        setProfileComplete(false);
      }
      else {
        setProfileComplete(true);
      }
    } catch (error) {
      console.error('Eroare server:', error);
      setError("Serviciul momentan nu funcționează. Te rugăm să revii.");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading-spinner">Se încarcă...</div>;
  }

  if (error) {
    return <div className="error-message p-4 text-red-500 bg-red-50 border border-red-200 rounded">{error}</div>;
  }
  // il redirectionam sa-si completeze profilul
  if (!profileComplete) {
    return <Navigate to="/pacient/complete-profile" replace />;
  }

  return <Outlet />;
}