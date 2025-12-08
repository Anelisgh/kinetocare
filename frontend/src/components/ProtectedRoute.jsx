import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ allowedRoles }) {
  const { isAuthenticated, userInfo } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!userInfo || !userInfo.roles) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && allowedRoles.length > 0) {
    const hasPermission = allowedRoles.some(role => userInfo.roles.includes(role));
    if (!hasPermission) {
      return <Navigate to="/unauthorized" replace />;
    }
  }

  return <Outlet />;
}