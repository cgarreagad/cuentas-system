import React, { useState, useEffect } from 'react';
import movimientoService from '../../services/movimientoService';
import cuentaService from '../../services/cuentaService';
import clienteService from '../../services/clienteService';

const MovimientoForm = ({ navigateTo }) => {
  const [formData, setFormData] = useState({
    tipoMovimiento: 'DEPOSITO',
    valor: '',
    numeroCuenta: ''
  });
  
  const [cuentas, setCuentas] = useState([]);
  const [cuentaSeleccionada, setCuentaSeleccionada] = useState(null);
  const [clientes, setClientes] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [cuentaPreseleccionada, setCuentaPreseleccionada] = useState(null);

  useEffect(() => {
    // Verificar si hay una cuenta preseleccionada en sessionStorage
    const storedCuentaId = sessionStorage.getItem('cuentaId');
    if (storedCuentaId) {
      setCuentaPreseleccionada(parseInt(storedCuentaId));
      setFormData(prev => ({
        ...prev,
        numeroCuenta: parseInt(storedCuentaId)
      }));
      sessionStorage.removeItem('cuentaId'); // Limpiar después de usarlo
    }
  }, []);

  useEffect(() => {
    const fetchCuentas = async () => {
      try {
        // Obtener todas las cuentas activas
        const data = await cuentaService.getAllCuentas();
        const cuentasActivas = data.filter(cuenta => cuenta.estado);
        setCuentas(cuentasActivas);
        
        // Obtener información de los clientes para mostrar en el selector de cuentas
        const clientesIds = [...new Set(cuentasActivas.map(cuenta => cuenta.clienteid))];
        
        const clientesData = await Promise.all(
          clientesIds.map(id => clienteService.getClienteById(id))
        );
        
        // Crear un objeto mapeado por ID para facilitar el acceso
        const clientesMap = {};
        clientesData.forEach(cliente => {
          clientesMap[cliente.clienteid] = cliente;
        });
        
        setClientes(clientesMap);
        
        // Si hay una cuenta preseleccionada, cargar sus datos
        if (cuentaPreseleccionada) {
          const cuentaInfo = cuentasActivas.find(c => c.numero_cuenta === cuentaPreseleccionada);
          if (cuentaInfo) {
            setCuentaSeleccionada(cuentaInfo);
          }
        }
      } catch (error) {
        console.error('Error al cargar cuentas:', error);
        setError('Error al cargar la lista de cuentas. Por favor, intente de nuevo.');
      }
    };

    fetchCuentas();
  }, [cuentaPreseleccionada]);

  // Cuando cambia la cuenta seleccionada
  useEffect(() => {
    if (formData.numeroCuenta) {
      const cuenta = cuentas.find(c => c.numero_cuenta === parseInt(formData.numeroCuenta));
      setCuentaSeleccionada(cuenta || null);
    } else {
      setCuentaSeleccionada(null);
    }
  }, [formData.numeroCuenta, cuentas]);

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    
    setFormData({
      ...formData,
      [name]: type === 'number' 
        ? parseFloat(value) || value
        : name === 'numeroCuenta'
          ? parseInt(value) || value
          : value
    });
  };

  const validateForm = () => {
    if (!formData.numeroCuenta) {
      setError('Debe seleccionar una cuenta.');
      return false;
    }
    if (!formData.valor || formData.valor <= 0) {
      setError('El valor debe ser mayor que cero.');
      return false;
    }
    // Para retiros, verificar que haya saldo suficiente
    if (formData.tipoMovimiento === 'RETIRO' && 
        cuentaSeleccionada && 
        formData.valor > cuentaSeleccionada.saldo_inicial) {
      setError(`Saldo insuficiente. El saldo actual es $${cuentaSeleccionada.saldo_inicial.toFixed(2)}.`);
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
      
      // Realizar el movimiento según el tipo
      if (formData.tipoMovimiento === 'DEPOSITO') {
        await movimientoService.realizarDeposito(formData);
        setSuccess('Depósito realizado correctamente.');
      } else {
        await movimientoService.realizarRetiro(formData);
        setSuccess('Retiro realizado correctamente.');
      }
      
      // Limpiar el formulario
      setFormData({
        tipoMovimiento: 'DEPOSITO',
        valor: '',
        numeroCuenta: cuentaPreseleccionada || ''
      });
      
      setLoading(false);
      
      // Redirigir después de un tiempo
      setTimeout(() => {
        navigateTo('movimientos');
        if (cuentaPreseleccionada) {
          sessionStorage.setItem('cuentaId', cuentaPreseleccionada);
        }
      }, 2000);
    } catch (error) {
      console.error('Error al realizar movimiento:', error);
      setError('Error al realizar el movimiento. Por favor, intente de nuevo.');
      setLoading(false);
    }
  };

  const getClienteNombre = (clienteId) => {
    return clientes[clienteId] ? clientes[clienteId].persona.nombre : '';
  };

  return (
    <div>
      <h2 className="page-title">
        Realizar Movimiento
      </h2>

      {error && <div className="alert alert-danger">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <div className="card">
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label" htmlFor="numeroCuenta">
                Cuenta *
              </label>
              <select
                id="numeroCuenta"
                name="numeroCuenta"
                className="form-control"
                value={formData.numeroCuenta}
                onChange={handleChange}
                disabled={cuentaPreseleccionada !== null} // No permitir cambiar si está preseleccionada
                required
              >
                <option value="">Seleccione una cuenta</option>
                {cuentas.map(cuenta => (
                  <option key={cuenta.numero_cuenta} value={cuenta.numero_cuenta}>
                    Cuenta {cuenta.numero_cuenta} - {cuenta.tipo_cuenta} - {getClienteNombre(cuenta.clienteid)}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="tipoMovimiento">
                Tipo de Movimiento *
              </label>
              <select
                id="tipoMovimiento"
                name="tipoMovimiento"
                className="form-control"
                value={formData.tipoMovimiento}
                onChange={handleChange}
                required
              >
                <option value="DEPOSITO">Depósito</option>
                <option value="RETIRO">Retiro</option>
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label" htmlFor="valor">
                Valor *
              </label>
              <input
                type="number"
                id="valor"
                name="valor"
                className="form-control"
                value={formData.valor}
                onChange={handleChange}
                min="0.01"
                step="0.01"
                required
              />
            </div>

            <div className="form-group">
              {cuentaSeleccionada && (
                <div className="saldo-info">
                  <label className="form-label">Saldo Actual:</label>
                  <p className="saldo-value">${cuentaSeleccionada.saldo_inicial.toFixed(2)}</p>
                  
                  {formData.tipoMovimiento === 'DEPOSITO' && formData.valor > 0 && (
                    <p className="saldo-nuevo">
                      Nuevo Saldo: ${(cuentaSeleccionada.saldo_inicial + parseFloat(formData.valor)).toFixed(2)}
                    </p>
                  )}
                  
                  {formData.tipoMovimiento === 'RETIRO' && formData.valor > 0 && (
                    <p className="saldo-nuevo">
                      Nuevo Saldo: ${Math.max(0, cuentaSeleccionada.saldo_inicial - parseFloat(formData.valor)).toFixed(2)}
                    </p>
                  )}
                </div>
              )}
            </div>
          </div>

          <div className="form-actions">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => {
                navigateTo('movimientos');
                if (cuentaPreseleccionada) {
                  sessionStorage.setItem('cuentaId', cuentaPreseleccionada);
                }
              }}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? 'Procesando...' : 'Realizar Movimiento'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default MovimientoForm;