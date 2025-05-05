import React, { useState, useEffect } from 'react';
import clienteService from '../../services/clienteService';

const ClientesList = ({ navigateTo }) => {
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    const fetchClientes = async () => {
      try {
        setLoading(true);
        const data = await clienteService.getAllClientes();
        setClientes(data);
        setLoading(false);
      } catch (error) {
        console.error('Error al cargar clientes:', error);
        setError('Error al cargar los clientes. Por favor, intente de nuevo.');
        setLoading(false);
      }
    };

    fetchClientes();
  }, []);

  const handleSearch = (e) => {
    setSearchTerm(e.target.value);
  };

  const filteredClientes = clientes.filter(cliente => 
    cliente.persona.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
    cliente.persona.identificacion.includes(searchTerm)
  );

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de que desea eliminar este cliente?')) {
      try {
        await clienteService.deleteCliente(id);
        setClientes(clientes.filter(cliente => cliente.clienteid !== id));
      } catch (error) {
        console.error('Error al eliminar cliente:', error);
        setError('Error al eliminar el cliente. Por favor, intente de nuevo.');
      }
    }
  };

  const handleViewCuentas = (clienteId) => {
    navigateTo('cuentas', null, false);
    // Actualizar el ID del cliente seleccionado
    sessionStorage.setItem('clienteId', clienteId);
  };

  if (loading) {
    return <div>Cargando clientes...</div>;
  }

  if (error) {
    return <div className="alert alert-danger">{error}</div>;
  }

  return (
    <div>
      <div className="page-actions">
        <h2 className="page-title">Gestión de Clientes</h2>
        <button 
          className="btn btn-primary" 
          onClick={() => navigateTo('cliente-form', null, false)}
        >
          <i className="fas fa-user-plus"></i> Nuevo Cliente
        </button>
      </div>

      <div className="card">
        <div className="search-container">
          <input
            type="text"
            placeholder="Buscar por nombre o identificación..."
            className="form-control"
            value={searchTerm}
            onChange={handleSearch}
          />
        </div>

        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Identificación</th>
                <th>Nombre</th>
                <th>Género</th>
                <th>Edad</th>
                <th>Dirección</th>
                <th>Teléfono</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredClientes.length === 0 ? (
                <tr>
                  <td colSpan="9" className="text-center">
                    No se encontraron clientes
                  </td>
                </tr>
              ) : (
                filteredClientes.map(cliente => (
                  <tr key={cliente.clienteid}>
                    <td>{cliente.clienteid}</td>
                    <td>{cliente.persona.identificacion}</td>
                    <td>{cliente.persona.nombre}</td>
                    <td>{cliente.persona.genero}</td>
                    <td>{cliente.persona.edad}</td>
                    <td>{cliente.persona.direccion}</td>
                    <td>{cliente.persona.telefono}</td>
                    <td>
                      <span className={`badge ${cliente.estado ? 'badge-success' : 'badge-danger'}`}>
                        {cliente.estado ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td>
                      <div className="table-actions">
                        <button
                          className="btn btn-sm btn-primary"
                          onClick={() => navigateTo('cliente-form', cliente.clienteid, true)}
                          title="Editar"
                        >
                          <i className="fas fa-edit"></i>
                        </button>
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDelete(cliente.clienteid)}
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

export default ClientesList;