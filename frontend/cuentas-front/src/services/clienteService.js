import { api } from './api';

const clienteService = {
  // Obtener todos los clientes
  getAllClientes: async () => {
    try {
      const response = await api.get('/clientes');
      const data = response.data || [];
      return Array.isArray(data) ? data.map(cliente => ({
        clienteid: cliente.clienteId,
        persona: {
          identificacion: cliente.persona.identificacion,
          nombre: cliente.persona.nombre,
          genero: cliente.persona.genero,
          edad: cliente.persona.edad,
          direccion: cliente.persona.direccion,
          telefono: cliente.persona.telefono
        },
        estado: cliente.estado
      })) : [];
    } catch (error) {
      console.error('Error al obtener clientes:', error);
      return [];
    }
  },
  
  // Obtener un cliente por su ID
  getClienteById: async (id) => {
    try {
      const response = await api.get(`/clientes/${id}`);
      const cliente = response.data;
      return cliente ? {
        clienteid: cliente.clienteId,
        persona: {
          identificacion: cliente.persona.identificacion,
          nombre: cliente.persona.nombre,
          genero: cliente.persona.genero,
          edad: cliente.persona.edad,
          direccion: cliente.persona.direccion,
          telefono: cliente.persona.telefono
        },
        estado: cliente.estado
      } : null;
    } catch (error) {
      console.error('Error al obtener cliente:', error);
      return null;
    }
  },
  
  // Obtener un cliente por su identificación
  getClienteByIdentificacion: async (identificacion) => {
    const response = await api.get(`/clientes/identificacion/${identificacion}`);
    return response.data;
  },
  
  // Crear un nuevo cliente
  createCliente: async (clienteData) => {
    const response = await api.post('/clientes', clienteData);
    return response.data;
  },
  
  // Actualizar un cliente existente
  updateCliente: async (id, clienteData) => {
    const response = await api.put(`/clientes/${id}`, clienteData);
    return response.data;
  },
  
  // Eliminar un cliente
  deleteCliente: async (id) => {
    return await api.delete(`/clientes/${id}`);
  }
};

export default clienteService;