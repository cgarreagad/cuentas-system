package com.cuentasbp.domain.services;

import com.cuentasbp.domain.entity.Persona;

public interface PersonaService {

    Persona findByIdentificacion(String identificacion);

    Persona save(Persona persona);

    Persona update(Persona persona);
}