package com.cuentasbp.domain.repositories;


import com.cuentasbp.domain.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaNumeroCuenta(Long numeroCuenta);

    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.numeroCuenta = :numeroCuenta AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha")
    List<Movimiento> findByCuentaAndFechaBetween(Long numeroCuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.cliente.clienteid = :clienteId AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.cuenta.numeroCuenta, m.fecha")
    List<Movimiento> findByClienteIdAndFechaBetween(Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
