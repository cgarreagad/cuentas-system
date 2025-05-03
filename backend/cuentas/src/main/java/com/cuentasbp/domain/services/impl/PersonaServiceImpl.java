package com.cuentasbp.domain.services.impl;


import com.cuentasbp.domain.entity.Persona;
import com.cuentasbp.domain.repositories.PersonaRepository;
import com.cuentasbp.domain.services.PersonaService;
import com.cuentasbp.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;

    @Override
    @Transactional(readOnly = true)
    public Persona findByIdentificacion(String identificacion) {
        return personaRepository.findById(identificacion)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "identificacion", identificacion));
    }

    @Override
    @Transactional
    public Persona save(Persona persona) {
        return personaRepository.save(persona);
    }

    @Override
    @Transactional
    public Persona update(Persona persona) {
        // Verifica si la persona existe
        Persona existingPersona = findByIdentificacion(persona.getIdentificacion());

        // Actualiza los campos
        existingPersona.setNombre(persona.getNombre());
        existingPersona.setGenero(persona.getGenero());
        existingPersona.setEdad(persona.getEdad());
        existingPersona.setDireccion(persona.getDireccion());
        existingPersona.setTelefono(persona.getTelefono());

        return personaRepository.save(existingPersona);
    }
}
