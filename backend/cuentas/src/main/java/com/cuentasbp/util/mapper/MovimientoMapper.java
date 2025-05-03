package com.cuentasbp.util.mapper;


import com.cuentasbp.domain.dto.request.MovimientoRequestDTO;
import com.cuentasbp.domain.dto.response.MovimientoResponseDTO;
import com.cuentasbp.domain.entity.Cuenta;
import com.cuentasbp.domain.entity.Movimiento;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class MovimientoMapper {

    private final ModelMapper modelMapper;

    public MovimientoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Movimiento toEntity(MovimientoRequestDTO dto, Cuenta cuenta, BigDecimal saldo) {
        Movimiento movimiento = modelMapper.map(dto, Movimiento.class);
        movimiento.setCuenta(cuenta);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setSaldo(saldo);
        return movimiento;
    }

    public MovimientoResponseDTO toDto(Movimiento entity) {
        MovimientoResponseDTO dto = modelMapper.map(entity, MovimientoResponseDTO.class);
        dto.setNumeroCuenta(entity.getCuenta().getNumeroCuenta());
        dto.setCliente(entity.getCuenta().getCliente().getPersona().getNombre());
        return dto;
    }
}