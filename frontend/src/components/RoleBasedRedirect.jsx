import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// Componenta care redirijeaza utilizatorul in functie de rolul sau (dupa login)
export default function RoleBasedRedirect() {
  const { isAuthenticated, userInfo } = useAuth();

    if (!isAuthenticated || !userInfo || !userInfo.roles) {
        return <Navigate to="/login" replace />;
    }
    
    if (userInfo.roles.includes('ADMIN')) {
        return <Navigate to="/admin/homepage" replace />;
    }

    if (userInfo.roles.includes('TERAPEUT')) {
        return <Navigate to="/terapeut/homepage" replace />;
    }

    if (userInfo.roles.includes('PACIENT')) {
        return <Navigate to="/pacient/homepage" replace />;
    }

    return <Navigate to="/unauthorized" replace />;
}