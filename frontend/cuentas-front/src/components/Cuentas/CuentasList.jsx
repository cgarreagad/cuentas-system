import React, { useState, useEffect } from 'react';
import cuentaService from '../../services/cuentaService';
import clienteService from '../../services/clienteService';

const CuentasList = ({ navigateTo }) => {
  const [cuentas, setCuentas] = useState([]);
  const [clientes, setClientes] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [clienteId, setClienteId] = useState(null);
  
  useEffect(() => {
    // Recuperar clienteId de sessionStorage si existe
    const storedClienteId = sessionStorage.getItem('clienteId');
    if (storedClienteId) {
      setClienteId(parseInt(storedClienteId));
      sessionStorage.removeItem('clienteId'); // Limpiar después de usarlo
    }
  }, []);

  useEffect(() => {
    const fetchCuentas = async () => {
      try {
        setLoading(true);
        let data;
        
        if (clienteId) {
          // Si hay un cliente seleccionado, mostrar solo sus cuentas
          data = await cuentaService.getCuentasByCliente(clienteId);
          
          // Obtener detalles del cliente
          const clienteData = await clienteService.getClienteById(clienteId);
          setClientes({ [clienteId]: clienteData });
        } else {
          // Mostrar todas las cuentas
          data = await cuentaService.getAllCuentas();
          
          // Obtener todos los clientes para mostrar sus nombres
          const clientesData = await clienteService.getAllClientes();
          
          // Crear un objeto mapeado por ID para facilitar el acceso
          const clientesMap = {};
          clientesData.forEach(cliente => {
            clientesMap[cliente.clienteid] = cliente;
          });
          
          setClientes(clientesMap);
        }
        
        setCuentas(data);
        setLoading(false);
      } catch (error) {
        console.error('Error al cargar cuentas:', error);
        setError('Error al cargar las cuentas. Por favor, intente de nuevo.');
        setLoading(false);
      }
    };

    fetchCuentas();
  }, [clienteId]);

  const handleSearch = (e) => {
    setSearchTerm(e.target.value);
  };

  const filteredCuentas = cuentas.filter(cuenta => {
    const numeroCuenta = cuenta?.numero_cuenta?.toString() || '';
    const nombreCliente = clientes[cuenta?.clienteid]?.persona?.nombre?.toLowerCase() || '';
    return numeroCuenta.includes(searchTerm) || nombreCliente.includes(searchTerm.toLowerCase());
  });

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de que desea eliminar esta cuenta?')) {
      try {
        await cuentaService.deleteCuenta(id);
        setCuentas(cuentas.filter(cuenta => cuenta.numero_cuenta !== id));
      } catch (error) {
        console.error('Error al eliminar cuenta:', error);
        setError('Error al eliminar la cuenta. Por favor, intente de nuevo.');
      }
    }
  };

  const getClienteNombre = (clienteId) => {
    return clientes[clienteId] ? clientes[clienteId].persona.nombre : 'Cliente no disponible';
  };

  const handleNuevaCuenta = () => {
    // Si hay un cliente seleccionado, usarlo al crear una nueva cuenta
    if (clienteId) {
      sessionStorage.setItem('clienteId', clienteId);
    }
    navigateTo('cuenta-form', null, false);
  };

  if (loading) {
    return <div>Cargando cuentas...</div>;
  }

  if (error) {
    return <div className="alert alert-danger">{error}</div>;
  }

  return (
    <div>
      <div className="page-actions">
        <h2 className="page-title">
          {clienteId ? `Cuentas de ${getClienteNombre(clienteId)}` : 'Gestión de Cuentas'}
        </h2>
        <button 
          className="btn btn-primary" 
          onClick={handleNuevaCuenta}
        >
          <i className="fas fa-plus-circle"></i> Nueva Cuenta
        </button>
      </div>

      <div className="card">
        <div className="search-container">
          <input
            type="text"
            placeholder="Buscar por número de cuenta o nombre de cliente..."
            className="form-control"
            value={searchTerm}
            onChange={handleSearch}
          />
        </div>

        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Número</th>
                {!clienteId && <th>Cliente</th>}
                <th>Tipo</th>
                <th>Saldo</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredCuentas.length === 0 ? (
                <tr>
                  <td colSpan={clienteId ? 5 : 6} className="text-center">
                    No se encontraron cuentas
                  </td>
                </tr>
              ) : (
                filteredCuentas.map(cuenta => (
                  <tr key={cuenta.numero_cuenta}>
                    <td>{cuenta.numero_cuenta}</td>
                    {!clienteId && (
                      <td>{getClienteNombre(cuenta.clienteid)}</td>
                    )}
                    <td>{cuenta.tipo_cuenta}</td>
                    <td>${cuenta.saldo_inicial.toFixed(2)}</td>
                    <td>
                      <span className={`badge ${cuenta.estado ? 'badge-success' : 'badge-danger'}`}>
                        {cuenta.estado ? 'Activa' : 'Inactiva'}
                      </span>
                    </td>
                    <td>
                      <div className="table-actions">
 
                        <button
                          className="btn btn-sm btn-primary"
                          onClick={() => navigateTo('cuenta-form', cuenta.numero_cuenta, true)}
                          title="Editar"
                        >
                          <i className="fas fa-edit"></i>
                        </button>
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDelete(cuenta.numero_cuenta)}
                          title="Eliminar"
                        >
                          <i className="fas fa-trash"></i>
                        </button>

                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default CuentasList;