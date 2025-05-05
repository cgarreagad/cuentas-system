import React, { useState, useEffect } from 'react';
import movimientoService from '../../services/movimientoService';
import cuentaService from '../../services/cuentaService';
import clienteService from '../../services/clienteService';

const MovimientosList = ({ navigateTo }) => {
  const [movimientos, setMovimientos] = useState([]);
  const [cuenta, setCuenta] = useState(null);
  const [cliente, setCliente] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [cuentaId, setCuentaId] = useState(null);
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');

  useEffect(() => {
    // Recuperar cuentaId de sessionStorage si existe
    const storedCuentaId = sessionStorage.getItem('cuentaId');
    if (storedCuentaId) {
      setCuentaId(parseInt(storedCuentaId));
      sessionStorage.removeItem('cuentaId'); // Limpiar después de usarlo
    }
  }, []);

  useEffect(() => {
    const fetchMovimientos = async () => {
      try {
        setLoading(true);
        let movimientosData;
        
        if (cuentaId) {
          // Si hay una cuenta seleccionada, mostrar solo sus movimientos
          movimientosData = await movimientoService.getMovimientosByCuenta(cuentaId);
          
          // Obtener detalles de la cuenta
          const cuentaData = await cuentaService.getCuentaById(cuentaId);
          setCuenta(cuentaData);
          
          // Obtener detalles del cliente
          const clienteData = await clienteService.getClienteById(cuentaData.clienteid);
          setCliente(clienteData);
        } else {
          // Mostrar todos los movimientos
          movimientosData = await movimientoService.getAllMovimientos();
        }
        
        // Ordenar movimientos por fecha (más recientes primero)
        movimientosData.sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
        
        setMovimientos(movimientosData);
        setLoading(false);
      } catch (error) {
        console.error('Error al cargar movimientos:', error);
        setError('Error al cargar los movimientos. Por favor, intente de nuevo.');
        setLoading(false);
      }
    };

    fetchMovimientos();
  }, [cuentaId]);

  const handleSearch = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleFechaInicioChange = (e) => {
    setFechaInicio(e.target.value);
  };

  const handleFechaFinChange = (e) => {
    setFechaFin(e.target.value);
  };

  const filteredMovimientos = movimientos.filter(movimiento => {
    // Filtrar por término de búsqueda (código o tipo)
    const matchesSearch = 
      movimiento.codigo.toString().includes(searchTerm) ||
      movimiento.tipoMovimiento.toLowerCase().includes(searchTerm.toLowerCase());
    
    // Filtrar por rango de fechas si se han especificado
    let matchesFechaInicio = true;
    let matchesFechaFin = true;
    
    if (fechaInicio) {
      const fechaMovimiento = new Date(movimiento.fecha);
      const fechaInicioObj = new Date(fechaInicio);
      fechaInicioObj.setHours(0, 0, 0, 0);
      matchesFechaInicio = fechaMovimiento >= fechaInicioObj;
    }
    
    if (fechaFin) {
      const fechaMovimiento = new Date(movimiento.fecha);
      const fechaFinObj = new Date(fechaFin);
      fechaFinObj.setHours(23, 59, 59, 999);
      matchesFechaFin = fechaMovimiento <= fechaFinObj;
    }
    
    return matchesSearch && matchesFechaInicio && matchesFechaFin;
  });

  const handleDownloadJSON = () => {
    const datosJSON = JSON.stringify(filteredMovimientos, null, 2);
    const blob = new Blob([datosJSON], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    
    const a = document.createElement('a');
    a.href = url;
    a.download = `movimientos${cuentaId ? `-cuenta-${cuentaId}` : ''}-${new Date().toISOString().split('T')[0]}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  const handleEstadoCuenta = () => {
    if (cuenta && cliente) {
      navigateTo('estado-cuenta');
      sessionStorage.setItem('clienteId', cliente.clienteid);
      sessionStorage.setItem('cuentaId', cuenta.numeroCuenta);
    } else {
      navigateTo('estado-cuenta');
    }
  };

  const handleNuevoMovimiento = () => {
    navigateTo('movimiento-form');
    if (cuentaId) {
      sessionStorage.setItem('cuentaId', cuentaId);
    }
  };

  if (loading) {
    return <div>Cargando movimientos...</div>;
  }

  if (error) {
    return <div className="alert alert-danger">{error}</div>;
  }

  return (
    <div>
      <div className="page-actions">
        <h2 className="page-title">
          {cuenta 
            ? `Movimientos de la Cuenta ${cuenta.numeroCuenta}` 
            : 'Gestión de Movimientos'}
        </h2>
        <div className="page-buttons">
          <button 
            className="btn btn-primary" 
            onClick={handleNuevoMovimiento}
          >
            <i className="fas fa-plus"></i> Nuevo
          </button>

        </div>
      </div>

      <div className="card">
        <div className="filtros-container">
          <div className="search-container">
            <input
              type="text"
              placeholder="Buscar por código o tipo..."
              className="form-control"
              value={searchTerm}
              onChange={handleSearch}
            />
          </div>
          
          <div className="fecha-filtros">
            <div className="form-group">
              <label className="form-label" htmlFor="fechaInicio">Desde:</label>
              <input
                type="date"
                id="fechaInicio"
                className="form-control"
                value={fechaInicio}
                onChange={handleFechaInicioChange}
              />
            </div>
            
            <div className="form-group">
              <label className="form-label" htmlFor="fechaFin">Hasta:</label>
              <input
                type="date"
                id="fechaFin"
                className="form-control"
                value={fechaFin}
                onChange={handleFechaFinChange}
              />
            </div>
          </div>
        </div>

        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Código</th>
                <th>Fecha</th>
                {!cuentaId && <th>Cuenta</th>}
                <th>Cliente</th>
                <th>Tipo</th>
                <th>Valor</th>
                <th>Saldo</th>
              </tr>
            </thead>
            <tbody>
              {filteredMovimientos.length === 0 ? (
                <tr>
                  <td colSpan={cuentaId ? 5 : 6} className="text-center">
                    No se encontraron movimientos
                  </td>
                </tr>
              ) : (
                filteredMovimientos.map(movimiento => (
                  <tr key={movimiento.codigo}>
                    <td>{movimiento.codigo}</td>
                    <td>{new Date(movimiento.fecha).toLocaleString()}</td>
                    {!cuentaId && (
                      <td>{movimiento.numeroCuenta}</td>
                    )}
                    <td>{movimiento.cliente}</td>
                    <td>
                      <span className={`badge ${movimiento.tipoMovimiento === 'DEPOSITO' ? 'badge-success' : 'badge-danger'}`}>
                        {movimiento.tipoMovimiento}
                      </span>
                    </td>
                    
                    <td>${movimiento.valor.toFixed(2)}</td>
                    <td>${movimiento.saldo.toFixed(2)}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        
        {filteredMovimientos.length > 0 && (
          <div className="card-actions">
            <button 
              className="btn btn-secondary" 
              onClick={handleDownloadJSON}
            >
              <i className="fas fa-download"></i> Descargar JSON
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default MovimientosList;