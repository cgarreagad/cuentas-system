import { api } from './api';

const movimientoService = {
  // Obtener todos los movimientos
  getAllMovimientos: async () => {
    const response = await api.get('/movimientos');
    return response.data || [];
  },
  
  // Obtener un movimiento por su código
  getMovimientoById: async (id) => {
    const response = await api.get(`/movimientos/${id}`);
    return response.data;
  },
  
  // Obtener movimientos por cuenta
  getMovimientosByCuenta: async (cuentaId) => {
    const response = await api.get(`/movimientos/cuenta/${cuentaId}`);
    return response.data || [];
  },
  
  // Realizar un depósito
  realizarDeposito: async (datos) => {
    const movimientoData = {
      tipoMovimiento: 'DEPOSITO',
      valor: datos.valor,
      numeroCuenta: datos.numeroCuenta
    };
    
    const response = await api.post('/movimientos', movimientoData);
    return response.data;
  },
  
  // Realizar un retiro
  realizarRetiro: async (datos) => {
    const movimientoData = {
      tipoMovimiento: 'RETIRO',
      valor: datos.valor,
      numeroCuenta: datos.numeroCuenta
    };
    
    const response = await api.post('/movimientos', movimientoData);
    return response.data;
  }
};

export default movimientoService;