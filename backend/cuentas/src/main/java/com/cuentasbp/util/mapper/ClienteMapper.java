package com.cuentasbp.util.mapper;


import com.cuentasbp.domain.dto.request.ClienteRequestDTO;
import com.cuentasbp.domain.dto.response.ClienteResponseDTO;
import com.cuentasbp.domain.entity.Cliente;
import com.cuentasbp.domain.entity.Persona;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    private final ModelMapper modelMapper;
    private final PersonaMapper personaMapper;



    public ClienteMapper(ModelMapper modelMapper, PersonaMapper personaMapper) {
        this.modelMapper = modelMapper;
        this.personaMapper = personaMapper;
    }

    public Cliente toEntity(ClienteRequestDTO dto, Persona persona) {
        Cliente cliente = modelMapper.map(dto, Cliente.class);
        cliente.setPersona(persona);
        return cliente;
    }

    public ClienteResponseDTO toDto(Cliente entity) {
        ClienteResponseDTO dto = modelMapper.map(entity, ClienteResponseDTO.class);
        dto.setClienteId(entity.getClienteid());
        dto.setPersona(personaMapper.toDto(entity.getPersona()));
        return dto;
    }
}
