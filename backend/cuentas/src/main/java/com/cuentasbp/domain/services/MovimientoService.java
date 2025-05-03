package com.cuentasbp.domain.services;



import com.cuentasbp.domain.entity.Movimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoService {

    List<Movimiento> findAll();

    Movimiento findByCodigo(Long codigo);

    List<Movimiento> findByNumeroCuenta(Long numeroCuenta);

    List<Movimiento> findByCuentaAndFechaBetween(Long numeroCuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Movimiento> findByClienteIdAndFechaBetween(Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Movimiento realizarMovimiento(String tipoMovimiento, BigDecimal valor, Long numeroCuenta);
}
