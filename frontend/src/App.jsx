import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import RoleBasedRedirect from './components/RoleBasedRedirect';
import ProfileGuard from './components/ProfileGuard';

import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import UnauthorizedPage from './pages/UnauthorizedPage';

import TerapeutLayout from './layouts/TerapeutLayout';
import PacientLayout from './layouts/PacientLayout';

import HomepageTerapeut from './pages/terapeut/HomepageTerapeut';
import ProfilTerapeut from './pages/terapeut/ProfilTerapeut';
import EvaluariTerapeut from './pages/terapeut/EvaluariTerapeut';
import EvolutiiTerapeut from './pages/terapeut/EvolutiiTerapeut';

import HomepagePacient from './pages/pacient/HomepagePacient';
import ProfilPacient from './pages/pacient/ProfilPacient';
import CompleteProfile from './pages/pacient/CompleteProfile';
import JurnalPacient from './pages/pacient/JurnalPacient';
import ProtectedRoute from './components/ProtectedRoute';

import AdminLayout from './layouts/AdminLayout';
import AdminLocatii from './pages/admin/AdminLocatii';
import AdminServicii from './pages/admin/AdminServicii';

import './styles/navbar.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/unauthorized" element={<UnauthorizedPage />} />

        <Route element={<ProtectedRoute allowedRoles={['TERAPEUT']} />}>
          <Route element={<TerapeutLayout />}>
            <Route path="/terapeut/homepage" element={<HomepageTerapeut />} />
            <Route path="/terapeut/profil" element={<ProfilTerapeut />} />
            <Route path="/terapeut/evaluari" element={<EvaluariTerapeut />} />
            <Route path="/terapeut/evolutii" element={<EvolutiiTerapeut />} />
          </Route>
        </Route>

        <Route element={<ProtectedRoute allowedRoles={['PACIENT']} />}>
          <Route path="/pacient/complete-profile" element={<CompleteProfile />} />
          {/* Daca profilul este complet poate accesa */}
          <Route element={<ProfileGuard />}>
            <Route element={<PacientLayout />}>
              <Route path="/pacient/homepage" element={<HomepagePacient />} />
              <Route path="/pacient/profil" element={<ProfilPacient />} />
              <Route path="/pacient/jurnal" element={<JurnalPacient />} />
            </Route>
          </Route>
        </Route>

        <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
          <Route element={<AdminLayout />}>
            <Route path="/admin/homepage" element={<Navigate to="/admin/locatii" replace />} />
            <Route path="/admin/locatii" element={<AdminLocatii />} />
            <Route path="/admin/servicii" element={<AdminServicii />} />
          </Route>
        </Route>

        <Route path="/homepage" element={<RoleBasedRedirect />} />
        <Route path="/" element={<Navigate to="/homepage" replace />} />
        <Route path="*" element={<Navigate to="/homepage" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;