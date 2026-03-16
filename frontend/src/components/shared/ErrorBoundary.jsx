import React from 'react';

/**
 * Error Boundary global React
 * 
 * Anterior nu exista nicio componentă ErrorBoundary. Orice eroare de runtime
 * la render (ex: NPE pe date de la API) provoca ecran complet alb, fără feedback.
 * 
 * Utilizare: învelește <App /> sau secțiuni sensibile cu <ErrorBoundary>
 */
export class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, info) {
    console.error('[ErrorBoundary] Eroare neașteptată prinsă:', error);
    console.error('[ErrorBoundary] Component stack:', info?.componentStack);
  }

  handleReload = () => {
    window.location.reload();
  };

  render() {
    if (this.state.hasError) {
      return (
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '100vh',
          padding: '2rem',
          textAlign: 'center',
          fontFamily: 'Inter, sans-serif',
          backgroundColor: '#f8f9fa',
          color: '#212529',
        }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>⚠️</div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: '600', marginBottom: '0.5rem' }}>
            A apărut o eroare neașteptată
          </h1>
          <p style={{ color: '#6c757d', marginBottom: '1.5rem', maxWidth: '480px' }}>
            Ne pare rău pentru inconveniență. Încearcă să reîncărci pagina.
            Dacă problema persistă, contactează echipa de suport.
          </p>
          <button
            onClick={this.handleReload}
            style={{
              padding: '0.6rem 1.5rem',
              backgroundColor: '#0d6efd',
              color: 'white',
              border: 'none',
              borderRadius: '0.375rem',
              cursor: 'pointer',
              fontSize: '1rem',
              fontWeight: '500',
            }}
          >
            Reîncarcă pagina
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
