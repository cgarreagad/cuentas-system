import React, { useState, useEffect } from 'react';
import clienteService from '../../services/clienteService';

const ClienteForm = ({ navigateTo, clienteId, isEditing }) => {
  const [formData, setFormData] = useState({
    persona: {
      identificacion: '',
      nombre: '',
      genero: 'M',
      edad: '',
      direccion: '',
      telefono: ''
    },
    contrasena: '',
    estado: true
  });
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  useEffect(() => {
    const fetchCliente = async () => {
      if (isEditing && clienteId) {
        try {
          setLoading(true);
          const data = await clienteService.getClienteById(clienteId);
          setFormData({
            persona: {
              identificacion: data.persona.identificacion || '',
              nombre: data.persona.nombre || '',
              genero: data.persona.genero || 'M',
              edad: data.persona.edad || '',
              direccion: data.persona.direccion || '',
              telefono: data.persona.telefono || ''
            },
            contrasena: '', // No se muestra la contraseña por seguridad
            estado: data.estado === undefined ? true : data.estado
          });
          setLoading(false);
        } catch (error) {
          console.error('Error al cargar datos del cliente:', error);
          setError('Error al cargar los datos del cliente. Por favor, intente de nuevo.');
          setLoading(false);
        }
      }
    };

    fetchCliente();
  }, [isEditing, clienteId]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    
    if (name.includes('persona.')) {
      const field = name.split('.')[1];
      setFormData({
        ...formData,
        persona: {
          ...formData.persona,
          [field]: type === 'checkbox' ? checked : value
        }
      });
    } else {
      setFormData({
        ...formData,
        [name]: type === 'checkbox' ? checked : value
      });
    }
  };

  const validateForm = () => {
    if (!formData.persona.identificacion) {
      setError('La identificación es obligatoria.');
      return false;
    }
    if (!formData.persona.nombre) {
      setError('El nombre es obligatorio.');
      return false;
    }
    if (!isEditing && !formData.contrasena) {
      setError('La contraseña es obligatoria.');
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
      
      if (isEditing) {
        const dataToSend = { ...formData };
        if (!dataToSend.contrasena) {
          delete dataToSend.contrasena; // No enviar contraseña vacía al actualizar
        }
        
        await clienteService.updateCliente(clienteId, dataToSend);
        setSuccess('Cliente actualizado correctamente.');
      } else {
        await clienteService.createCliente(formData);
        setSuccess('Cliente creado correctamente.');
        
        // Limpiar el formulario después de crear
        setFormData({
          persona: {
            identificacion: '',
            nombre: '',
            genero: 'M',
            edad: '',
            direccion: '',
            telefono: ''
          },
          contrasena: '',
          estado: true
        });
      }
      
      setLoading(false);
      
      // Redirigir después de un tiempo
      setTimeout(() => {
        navigateTo('clientes');
      }, 2000);
    } catch (error) {
      console.error('Error al guardar cliente:', error);
      setError('Error al guardar los datos. Por favor, intente de nuevo.');
      setLoading(false);
    }
  };

  if (loading && isEditing) {
    return <div>Cargando datos del cliente...</div>;
  }

  return (
    <div>
      <h2 className="page-title">
        {isEditing ? 'Editar Cliente' : 'Nuevo Cliente'}
      </h2>

      {error && <div className="alert alert-danger" role="alert">{error}</div>}
      {success && <div className="alert alert-success" role="alert">{success}</div>}

      <div className="card">
        <form onSubmit={handleSubmit}>
          <h3>Datos Personales</h3>
          
          <div className="form-row">
            <div className="form-group">
              <label className="form-label" htmlFor="identificacion">
                Identificación *
              </label>
              <input
                type="text"
                id="identificacion"
                name="persona.identificacion"
                className="form-control"
                value={formData.persona.identificacion}
                onChange={handleChange}
                disabled={isEditing} // No permitir cambiar la identificación al editar
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="nombre">
                Nombre Completo *
              </label>
              <input
                type="text"
                id="nombre"
                name="persona.nombre"
                className="form-control"
                value={formData.persona.nombre}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label" htmlFor="genero">
                Género
              </label>
              <select
                id="genero"
                name="persona.genero"
                className="form-control"
                value={formData.persona.genero}
                onChange={handleChange}
              >
                <option value="M">Masculino</option>
                <option value="F">Femenino</option>
                <option value="O">Otro</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="edad">
                Edad
              </label>
              <input
                type="number"
                id="edad"
                name="persona.edad"
                className="form-control"
                value={formData.persona.edad}
                onChange={handleChange}
                min="0"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label" htmlFor="direccion">
                Dirección
              </label>
              <input
                type="text"
                id="direccion"
                name="persona.direccion"
                className="form-control"
                value={formData.persona.direccion}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="telefono">
                Teléfono
              </label>
              <input
                type="text"
                id="telefono"
                name="persona.telefono"
                className="form-control"
                value={formData.persona.telefono}
                onChange={handleChange}
              />
            </div>
          </div>

          <h3>Datos de Acceso</h3>
          
          <div className="form-row">
            <div className="form-group">
              <label className="form-label" htmlFor="contrasena">
                Contraseña {isEditing ? '(Dejar en blanco para mantener la actual)' : '*'}
              </label>
              <input
                type="password"
                id="contrasena"
                name="contrasena"
                className="form-control"
                value={formData.contrasena}
                onChange={handleChange}
                required={!isEditing}
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
                <label htmlFor="estado">Activo</label>
              </div>
            </div>
          </div>

          <div className="form-actions">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => navigateTo('clientes')}
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

export default ClienteForm;