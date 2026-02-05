import React from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import '../styles/painel.scss'; 

interface PainelProps {
  onLogout: () => void;
}

const Painel: React.FC<PainelProps> = ({ onLogout }) => {
  const navigate = useNavigate();
  const location = useLocation();


  const isHome = location.pathname === '/painel' || location.pathname === '/painel/';

 
  const menuItems = [
    { label: 'Calendário', iconPath: '/icons/calendar.png', path: 'calendario' },
    { label: 'Lembretes', iconPath: '/icons/reminders.png', path: 'lembretes' },
    { label: 'Notas', iconPath: '/icons/notes.png', path: 'notas' },
  ];

  // Recupera o nome salvo durante o Login
  const nome = localStorage.getItem('@Zotion:usuario') || 'Usuário';

  return (
    <div className="dashboard-container"> 
      <header className="main-header">
        <div className="brand" onClick={() => navigate('/painel')}>
          <img src="/logo.png" alt="Zotion Logo" className="brand-logo" />
        </div>
        <div className="user-info">
          <span>Olá, {nome} </span>
          <button className="logout-btn" onClick={onLogout}>Sair</button>
        </div>
      </header>

      <main className="main-content-area">
        {isHome ? (
          <div className="menu-selection">
            <div>
              <h3>Para onde vamos hoje?</h3>
            </div>
            <div className="cards-grid">
              {menuItems.map((item) => (
                <div key={item.path} className="menu-card" onClick={() => navigate(item.path)}>
                  <img src={item.iconPath} alt={item.label} className="icon-img" />
                  <span className="label">{item.label}</span>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <div className="page-wrapper-full">
            <div className="navigation-bar">
              <button className="back-to-menu" onClick={() => navigate('/painel')}>
                 Retornar ao Menu
              </button>
            </div>
            <div className="page-content">
              <Outlet />
            </div>
          </div>
        )}
      </main>

      <footer className="main-footer">
        <div className="footer-credits">
          <strong>Giovane Godoi</strong>
          <p>Zotion &copy; 2026 | Todos os direitos reservados</p>
        </div>
      </footer>
    </div>
  );
};

export default Painel;