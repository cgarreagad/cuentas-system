// URL base de la API
//const BASE_URL = 'http://192.168.0.126:8080/api';
const BASE_URL = '/api';

// Configuración por defecto para las peticiones
const defaultConfig = {
  headers: {
    'Content-Type': 'application/json'
  }
};

// Funciones genéricas para realizar peticiones HTTP
export const api = {
  // GET: Obtener datos
  async get(endpoint) {
    try {
      const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: 'GET',
        ...defaultConfig
      });
      
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error('Error en petición GET:', error);
      throw error;
    }
  },
  
  // POST: Crear nuevo recurso
  async post(endpoint, data) {
    try {
      const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: 'POST',
        ...defaultConfig,
        body: JSON.stringify(data)
      });
      
      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || `Error HTTP: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error('Error en petición POST:', error);
      throw error;
    }
  },
  
  // PUT: Actualizar recurso existente
  async put(endpoint, data) {
    try {
      const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: 'PUT',
        ...defaultConfig,
        body: JSON.stringify(data)
      });
      
      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || `Error HTTP: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error('Error en petición PUT:', error);
      throw error;
    }
  },
  
  // DELETE: Eliminar recurso
  async delete(endpoint) {
    try {
      const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: 'DELETE',
        ...defaultConfig
      });
      
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }
      
      return true;
    } catch (error) {
      console.error('Error en petición DELETE:', error);
      throw error;
    }
  },
  
  // GET para descargar archivo (PDF, etc.)
  async getBlob(endpoint) {
    try {
      const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: 'GET',
        ...defaultConfig
      });
      
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }
      
      return await response.blob();
    } catch (error) {
      console.error('Error en petición GET (blob):', error);
      throw error;
    }
  }
};

export default api;