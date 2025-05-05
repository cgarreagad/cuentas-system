import React from 'react';

const Sidebar = ({ currentPage, navigateTo }) => {
  const isActive = (page) => {
    if (page === currentPage) return 'menu-item active';
    if (currentPage.startsWith(page + '-')) return 'menu-item active';
    return 'menu-item';
  };

  return (
    <div className="sidebar">
      <ul className="sidebar-menu">
        <li 
          className={isActive('clientes')}
          onClick={() => navigateTo('clientes')}
        >
          <i className="fas fa-users"></i>
          <span>Clientes</span>
        </li>
        <li 
          className={isActive('cuentas')}
          onClick={() => navigateTo('cuentas')}
        >
          <i className="fas fa-credit-card"></i>
          <span>Cuentas</span>
        </li>
        <li 
          className={isActive('movimientos')}
          onClick={() => navigateTo('movimientos')}
        >
          <i className="fas fa-exchange-alt"></i>
          <span>Movimientos</span>
        </li>
        <li 
          className={isActive('estado-cuenta')}
          onClick={() => navigateTo('estado-cuenta')}
        >
          <i className="fas fa-file-invoice-dollar"></i>
          <span>Estado de Cuenta</span>
        </li>
      </ul>
    </div>
  );
};

export default Sidebar;