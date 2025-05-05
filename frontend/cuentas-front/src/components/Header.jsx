import React from 'react';

const Header = ({ currentPage, navigateTo }) => {
  const getPageTitle = () => {
    switch (currentPage) {
      case 'dashboard':
        return 'Panel Principal';
      case 'clientes':
        return 'Gestión de Clientes';
      case 'cliente-form':
        return 'Formulario de Cliente';
      case 'cuentas':
        return 'Gestión de Cuentas';
      case 'cuenta-form':
        return 'Formulario de Cuenta';
      case 'movimientos':
        return 'Gestión de Movimientos';
      case 'movimiento-form':
        return 'Realizar Movimiento';
      default:
        return 'Sistema Cuentas';
    }
  };

  const showBackButton = () => {
    return currentPage !== 'home';
  };

  const handleBack = () => {
    switch (currentPage) {
      case 'cliente-form':
        navigateTo('clientes');
        break;
      case 'cuenta-form':
        navigateTo('cuentas');
        break;
      case 'movimiento-form':
        navigateTo('movimientos');
        break;
      case 'clientes':
      case 'cuentas':
      case 'movimientos':
        navigateTo('home');
        break;
      default:
        navigateTo('home');
        break;
    }
  };

  return (
    <header className="header">
      <div className="header-nav">
        {showBackButton() && (
          <button className="back-button" onClick={handleBack}>
            <i className="fas fa-arrow-left"></i>
          </button>
        )}
        <h1 className="header-title">{getPageTitle()}</h1>&nbsp;&nbsp;
        {showBackButton() && (
          <button className="home-button" onClick={() => navigateTo('home')}>
            <i className="fas fa-home"></i>
          </button>
        )}
      </div>
    </header>
  );
};

export default Header;