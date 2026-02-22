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
import PacientiTerapeut from './pages/terapeut/PacientiTerapeut';
import FisaPacient from './pages/terapeut/FisaPacient';

import HomepagePacient from './pages/pacient/HomepagePacient';
import ProfilPacient from './pages/pacient/ProfilPacient';
import CompleteProfile from './pages/pacient/CompleteProfile';
import JurnalPacient from './pages/pacient/JurnalPacient';
import ProgramariPacient from './pages/pacient/ProgramariPacient';
import ProtectedRoute from './components/ProtectedRoute';

import AdminLayout from './layouts/AdminLayout';
import AdminLocatii from './pages/admin/AdminLocatii';
import AdminServicii from './pages/admin/AdminServicii';
import AdminUsers from './pages/admin/AdminUsers';
import AdminHomepage from './pages/admin/AdminHomepage';
import AdminStatistici from './pages/admin/AdminStatistici';

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
            <Route path="/terapeut/pacienti" element={<PacientiTerapeut />} />
            <Route path="/terapeut/pacienti/:pacientId" element={<FisaPacient />} />
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
              <Route path="/pacient/programari" element={<ProgramariPacient />} />
            </Route>
          </Route>
        </Route>

        <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
          <Route element={<AdminLayout />}>
            <Route path="/admin/homepage" element={<AdminHomepage />} />
            <Route path="/admin/locatii" element={<AdminLocatii />} />
            <Route path="/admin/servicii" element={<AdminServicii />} />
            <Route path="/admin/utilizatori" element={<AdminUsers />} />
            <Route path="/admin/statistici" element={<AdminStatistici />} />
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