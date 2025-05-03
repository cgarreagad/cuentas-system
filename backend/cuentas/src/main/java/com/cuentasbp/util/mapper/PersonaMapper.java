package com.cuentasbp.util.mapper;


import com.cuentasbp.domain.dto.request.PersonaRequestDTO;
import com.cuentasbp.domain.dto.response.PersonaResponseDTO;
import com.cuentasbp.domain.entity.Persona;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {

    private final ModelMapper modelMapper;

    public PersonaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Persona toEntity(PersonaRequestDTO dto) {
        Persona persona = modelMapper.map(dto, Persona.class);
        if (dto.getGenero() != null) {
            persona.setGenero(dto.getGenero().charAt(0));
        }
        return persona;
    }

    public PersonaResponseDTO toDto(Persona entity) {
        return modelMapper.map(entity, PersonaResponseDTO.class);
    }
}