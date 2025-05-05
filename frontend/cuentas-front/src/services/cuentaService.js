import { api } from './api';

const cuentaService = {
  // Obtener todas las cuentas
  getAllCuentas: async () => {
    try {
      const response = await api.get('/cuentas');
      const data = response.data || [];
      return Array.isArray(data) ? data.map(cuenta => ({
        numero_cuenta: cuenta.numeroCuenta,
        tipo_cuenta: cuenta.tipoCuenta,
        saldo_inicial: cuenta.saldoInicial,
        estado: cuenta.estado,
        clienteid: cuenta.cliente?.clienteId
      })) : [];
    } catch (error) {
      console.error('Error al obtener cuentas:', error);
      return [];
    }
  },
  
  // Obtener una cuenta por su número
  getCuentaById: async (id) => {
    try {
      const response = await api.get(`/cuentas/${id}`);
      const cuenta = response.data;
      return cuenta ? {
        numero_cuenta: cuenta.numeroCuenta,
        tipo_cuenta: cuenta.tipoCuenta,
        saldo_inicial: cuenta.saldoInicial,
        estado: cuenta.estado,
        clienteid: cuenta.cliente?.clienteId
      } : null;
    } catch (error) {
      console.error('Error al obtener cuenta:', error);
      return null;
    }
  },
  
  // Obtener cuentas por cliente
  getCuentasByCliente: async (clienteId) => {
    try {
      const response = await api.get(`/cuentas/cliente/${clienteId}`);
      const data = response.data || [];
      return Array.isArray(data) ? data.map(cuenta => ({
        numero_cuenta: cuenta.numeroCuenta,
        tipo_cuenta: cuenta.tipoCuenta,
        saldo_inicial: cuenta.saldoInicial,
        estado: cuenta.estado,
        clienteid: cuenta.cliente?.clienteId
      })) : [];
    } catch (error) {
      console.error('Error al obtener cuentas del cliente:', error);
      return [];
    }
  },
  
  // Crear una nueva cuenta
  createCuenta: async (cuentaData) => {
    const response = await api.post('/cuentas', cuentaData);
    return response.data;
  },
  
  // Actualizar una cuenta existente
  updateCuenta: async (id, cuentaData) => {
    const response = await api.put(`/cuentas/${id}`, cuentaData);
    return response.data;
  },
  
  // Eliminar una cuenta
  deleteCuenta: async (id) => {
    return await api.delete(`/cuentas/${id}`);
  },
  
  // Generar estado de cuenta
  generarEstadoCuenta: async (datos) => {
    const response = await api.post('/cuentas/estado-cuenta', datos);
    return response.data;
  },
  
  // Descargar estado de cuenta en PDF
  descargarEstadoCuenta: async (datos) => {
    // Obtener el estado de cuenta como JSON
    const response = await api.post('/cuentas/estado-cuenta', datos);
    const respuesta = response.data;
    
    // Si la respuesta incluye el PDF en base64, convertirlo a Blob
    if (respuesta && respuesta.pdfBase64) {
      const byteCharacters = atob(respuesta.pdfBase64);
      const byteNumbers = new Array(byteCharacters.length);
      
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      
      const byteArray = new Uint8Array(byteNumbers);
      const blob = new Blob([byteArray], { type: 'application/pdf' });
      
      return {
        json: respuesta,
        pdf: blob
      };
    }
    
    return {
      json: respuesta,
      pdf: null
    };
  }
};

export default cuentaService;