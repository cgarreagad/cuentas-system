import { useState } from 'react'
import Header from './components/Header';
import Sidebar from './components/Sidebar';
import Footer from './components/Footer';
import Home from './components/Home.jsx';
import ClientesList from './components/Clientes/ClientesList.jsx';
import ClienteForm from './components/Clientes/ClienteForm.jsx';
import CuentasList from './components/Cuentas/CuentasList.jsx';
import CuentaForm from './components/Cuentas/CuentaForm.jsx';
import MovimientoForm from './components/Movimientos/MovimientoForm.jsx';
import MovimientosList from './components/Movimientos/MovimientosList.jsx';
import './assets/css/styles.css';

function App() {
  const [currentPage, setCurrentPage] = useState('home');
  const [selectedId, setSelectedId] = useState(null);
  const [clienteId, setClienteId] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
    const navigateTo = (page, id = null, isEdit = false) => {
    setCurrentPage(page);
    
    if (id !== undefined) {
      setSelectedId(id);
    }
    
    if (isEdit !== undefined) {
      setIsEditing(isEdit);
    }
  };

  const renderContent = () => {
    switch (currentPage) {
      case 'home':
        return <Home navigateTo={navigateTo} />;
      case 'clientes':
        return <ClientesList navigateTo={navigateTo} />;
      case 'cliente-form':
        return <ClienteForm 
          navigateTo={navigateTo} 
          clienteId={selectedId} 
          isEditing={isEditing} 
        />;
      case 'cuentas':
        return <CuentasList 
          navigateTo={navigateTo} 
          clienteId={clienteId}
        />;
      
      case 'cuenta-form':
        return <CuentaForm 
          navigateTo={navigateTo} 
          cuentaId={selectedId} 
          clienteId={clienteId}
          isEditing={isEditing} 
        />;        

      case 'movimientos':
        return <MovimientosList 
          navigateTo={navigateTo} 
          cuentaId={selectedId}
        />;
      case 'movimiento-form':
        return <MovimientoForm 
          navigateTo={navigateTo} 
          cuentaId={selectedId}
        />;
      default:
        return <Home navigateTo={navigateTo} />;
    }
  };

  return (
    <div className="container">
      <Header currentPage={currentPage} navigateTo={navigateTo} />
      <div className="main-content">
        <Sidebar currentPage={currentPage} navigateTo={navigateTo} />
        <div className="content">
          {renderContent()} 
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default App
