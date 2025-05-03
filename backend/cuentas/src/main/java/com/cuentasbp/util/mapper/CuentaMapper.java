package com.cuentasbp.util.mapper;


import com.cuentasbp.domain.dto.request.CuentaRequestDTO;
import com.cuentasbp.domain.dto.response.CuentaResponseDTO;
import com.cuentasbp.domain.entity.Cliente;
import com.cuentasbp.domain.entity.Cuenta;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CuentaMapper {

    private final ModelMapper modelMapper;
    private final ClienteMapper clienteMapper;

    public CuentaMapper(ModelMapper modelMapper, ClienteMapper clienteMapper) {
        this.modelMapper = modelMapper;
        this.clienteMapper = clienteMapper;
    }

    public Cuenta toEntity(CuentaRequestDTO dto, Cliente cliente) {
        Cuenta cuenta = modelMapper.map(dto, Cuenta.class);
        cuenta.setCliente(cliente);
        return cuenta;
    }

    public CuentaResponseDTO toDto(Cuenta entity) {
        CuentaResponseDTO dto = modelMapper.map(entity, CuentaResponseDTO.class);
        if (entity.getCliente() != null) {
            dto.setCliente(clienteMapper.toDto(entity.getCliente()));
        }
        return dto;
    }
}