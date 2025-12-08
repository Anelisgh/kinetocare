import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/auth.css';

export default function UnauthorizedPage() {
  return (
    <div className="auth-container">
      <h1 className="auth-title" style={{color: '#e53e3e'}}>Acces Restricționat</h1>
      <p className="error-message">
        Nu ai permisiunea necesară pentru a accesa această pagină.
      </p>
      <Link to="/homepage" className="auth-link">Înapoi la Home</Link>
      <Link to="/login" className="auth-link">Autentificare</Link>
    </div>
  );
}