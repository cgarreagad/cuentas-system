package com.cuentasbp.domain.services.impl;


import com.cuentasbp.domain.entity.Cuenta;
import com.cuentasbp.domain.entity.Movimiento;
import com.cuentasbp.domain.entity.TipoMovimiento;
import com.cuentasbp.domain.repositories.MovimientoRepository;
import com.cuentasbp.domain.services.CuentaService;
import com.cuentasbp.domain.services.MovimientoService;
import com.cuentasbp.util.exception.ResourceNotFoundException;
import com.cuentasbp.util.exception.SaldoNoDisponibleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaService cuentaService;

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> findAll() {
        return movimientoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Movimiento findByCodigo(Long codigo) {
        return movimientoRepository.findById(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento", "codigo", codigo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> findByNumeroCuenta(Long numeroCuenta) {
        return movimientoRepository.findByCuentaNumeroCuenta(numeroCuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> findByCuentaAndFechaBetween(Long numeroCuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoRepository.findByCuentaAndFechaBetween(numeroCuenta, fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> findByClienteIdAndFechaBetween(Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoRepository.findByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin);
    }

    @Override
    @Transactional
    public Movimiento realizarMovimiento(String tipoMovimientoStr, BigDecimal valor, Long numeroCuenta) {
        Cuenta cuenta = cuentaService.findByNumeroCuenta(numeroCuenta);
        TipoMovimiento tipoMovimiento = TipoMovimiento.valueOf(tipoMovimientoStr);

        BigDecimal saldoActual = cuenta.getSaldoInicial();
        BigDecimal nuevoSaldo;

        if (tipoMovimiento == TipoMovimiento.DEPOSITO) {
            nuevoSaldo = saldoActual.add(valor);
        } else { // RETIRO
            if (saldoActual.compareTo(BigDecimal.ZERO) == 0 || saldoActual.compareTo(valor) < 0) {
                throw new SaldoNoDisponibleException();
            }
            nuevoSaldo = saldoActual.subtract(valor);
        }

        // Actualizar saldo de la cuenta
        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaService.save(cuenta);

        // Crear el movimiento
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(tipoMovimientoStr);
        movimiento.setValor(valor);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);

        return movimientoRepository.save(movimiento);
    }
}
