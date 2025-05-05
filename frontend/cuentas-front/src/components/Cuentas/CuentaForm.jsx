import React, { useState, useEffect } from 'react';
import cuentaService from '../../services/cuentaService';
import clienteService from '../../services/clienteService';

const CuentaForm = ({ navigateTo, cuentaId, isEditing }) => {
  const [formData, setFormData] = useState({
    tipoCuenta: 'AHORRO',
    saldoInicial: '',
    estado: true,
    clienteId: ''
  });
  
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [clientePreseleccionado, setClientePreseleccionado] = useState(null);

  useEffect(() => {
    // Verificar si hay un cliente preseleccionado en sessionStorage
    const storedClienteId = sessionStorage.getItem('clienteId');
    if (storedClienteId) {
      setClientePreseleccionado(parseInt(storedClienteId));
      setFormData(prev => ({
        ...prev,
        clienteId: parseInt(storedClienteId)
      }));
      sessionStorage.removeItem('clienteId'); // Limpiar después de usarlo
    }
  }, []);

  useEffect(() => {
    const fetchClientes = async () => {
      try {
        const data = await clienteService.getAllClientes();
        // Filtrar solo clientes activos
        const clientesActivos = data.filter(cliente => cliente.estado);
        setClientes(clientesActivos);
      } catch (error) {
        console.error('Error al cargar clientes:', error);
        setError('Error al cargar la lista de clientes. Por favor, intente de nuevo.');
      }
    };

    fetchClientes();
  }, []);

  useEffect(() => {
    const fetchCuenta = async () => {
      if (isEditing && cuentaId) {
        try {
          setLoading(true);
          const data = await cuentaService.getCuentaById(cuentaId);
          setFormData({
            tipoCuenta: data.tipo_cuenta || 'AHORRO',
            saldoInicial: data.saldo_inicial || '',
            estado: data.estado === undefined ? true : data.estado,
            clienteId: data.clienteid || ''
          });
          setLoading(false);
        } catch (error) {
          console.error('Error al cargar datos de la cuenta:', error);
          setError('Error al cargar los datos de la cuenta. Por favor, intente de nuevo.');
          setLoading(false);
        }
      }
    };

    fetchCuenta();
  }, [isEditing, cuentaId]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    
    setFormData({
      ...formData,
      [name]: type === 'checkbox' 
        ? checked 
        : name === 'clienteId' || name === 'saldoInicial'
          ? parseFloat(value) || value
          : value
    });
  };

  const validateForm = () => {
    if (!formData.clienteId) {
      setError('Debe seleccionar un cliente.');
      return false;
    }
    if (formData.saldoInicial === '') {
      setError('El saldo inicial es obligatorio.');
      return false;
    }
    if (isNaN(parseFloat(formData.saldoInicial))) {
      setError('El saldo inicial debe ser un número válido.');
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    try {
      setLoading(true);
      setError(null);
      
      // Asegurarse de que los valores numéricos sean números
      const dataToSend = {
        ...formData,
        saldoInicial: parseFloat(formData.saldoInicial),
        clienteId: parseInt(formData.clienteId)
      };
      
      if (isEditing) {
        await cuentaService.updateCuenta(cuentaId, dataToSend);
        setSuccess('Cuenta actualizada correctamente.');
      } else {
        await cuentaService.createCuenta(dataToSend);
        setSuccess('Cuenta creada correctamente.');
        
        // Limpiar el formulario después de crear
        setFormData({
          tipoCuenta: 'AHORRO',
          saldoInicial: '',
          estado: true,
          clienteId: clientePreseleccionado || ''
        });
      }
      
      setLoading(false);
      
      // Redirigir después de un tiempo
      setTimeout(() => {
        navigateTo('cuentas');
      }, 2000);
    } catch (error) {
      console.error('Error al guardar cuenta:', error);
      setError('Error al guardar los datos. Por favor, intente de nuevo.');
      setLoading(false);
    }
  };

  if (loading && isEditing) {
    return <div>Cargando datos de la cuenta...</div>;
  }

  return (
    <div>
      <h2 className="page-title">
        {isEditing ? 'Editar Cuenta' : 'Nueva Cuenta'}
      </h2>

      {error && <div className="alert alert-danger">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <div className="card">
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label" htmlFor="clienteId">
                Cliente *
              </label>
              <select
                id="clienteId"
                name="clienteId"
                className="form-control"
                value={formData.clienteId}
                onChange={handleChange}
                disabled={isEditing || clientePreseleccionado !== null} // No permitir cambiar el cliente al editar
                required
              >
                <option value="">Seleccione un cliente</option>
                {clientes.map(cliente => (
                  <option key={cliente.clienteid} value={cliente.clienteid}>
                    {cliente.persona.nombre} ({cliente.persona.identificacion})
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="tipoCuenta">
                Tipo de Cuenta *
              </label>
              <select
                id="tipoCuenta"
                name="tipoCuenta"
                className="form-control"
                value={formData.tipoCuenta}
                onChange={handleChange}
                required
              >
                <option value="AHORRO">Cuenta de Ahorro</option>
                <option value="CORRIENTE">Cuenta Corriente</option>
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label" htmlFor="saldoInicial">
                Saldo Inicial *
              </label>
              <input
                type="number"
                id="saldoInicial"
                name="saldoInicial"
                className="form-control"
                value={formData.saldoInicial}
                onChange={handleChange}
                min="0"
                step="0.01"
                disabled={isEditing} // No permitir cambiar el saldo inicial al editar
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="estado">
                Estado
              </label>
              <div className="checkbox-container">
                <input
                  type="checkbox"
                  id="estado"
                  name="estado"
                  checked={formData.estado}
                  onChange={handleChange}
                />
                <label htmlFor="estado">Activa</label>
              </div>
            </div>
          </div>

          <div className="form-actions">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => navigateTo('cuentas')}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? 'Guardando...' : 'Guardar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CuentaForm;