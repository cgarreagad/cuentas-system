package com.cuentasbp.domain.services.impl;


import com.cuentasbp.domain.dto.response.EstadoCuentaResponseDTO;
import com.cuentasbp.domain.dto.response.MovimientoResponseDTO;
import com.cuentasbp.domain.entity.Cliente;
import com.cuentasbp.domain.entity.Cuenta;
import com.cuentasbp.domain.entity.Movimiento;
import com.cuentasbp.domain.services.ClienteService;
import com.cuentasbp.domain.services.CuentaService;
import com.cuentasbp.domain.services.MovimientoService;
import com.cuentasbp.domain.services.ReporteService;
import com.cuentasbp.util.PdfGenerator;
import com.cuentasbp.util.exception.ResourceNotFoundException;
import com.cuentasbp.util.mapper.MovimientoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ClienteService clienteService;
    private final CuentaService cuentaService;
    private final MovimientoService movimientoService;
    private final MovimientoMapper movimientoMapper;
    private final PdfGenerator pdfGenerator;

    @Override
    @Transactional(readOnly = true)
    public EstadoCuentaResponseDTO generarEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        // Buscar el cliente
        Cliente cliente = clienteService.findById(clienteId);
        if (cliente == null) {
            throw new ResourceNotFoundException("Cliente", "id", clienteId);
        }

        // Obtener las cuentas del cliente
        List<Cuenta> cuentas = cuentaService.findByClienteId(clienteId);

        // Fechas con hora
        LocalDateTime fechaInicioDateTime = fechaInicio.atStartOfDay();
        LocalDateTime fechaFinDateTime = fechaFin.atTime(LocalTime.MAX);

        // Obtener los movimientos en el rango de fechas
        List<Movimiento> movimientos = movimientoService.findByClienteIdAndFechaBetween(
                clienteId, fechaInicioDateTime, fechaFinDateTime);

        // Calcular totales
        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;

        // Mapear movimientos a DTOs
        List<MovimientoResponseDTO> movimientoDTOs = movimientos.stream()
                .map(movimientoMapper::toDto)
                .collect(Collectors.toList());

        // Calcular totales de créditos y débitos
        for (Movimiento movimiento : movimientos) {
            if ("DEPOSITO".equals(movimiento.getTipoMovimiento())) {
                totalCreditos = totalCreditos.add(movimiento.getValor());
            } else {
                totalDebitos = totalDebitos.add(movimiento.getValor());
            }
        }

        // Crear estado de cuenta por cada cuenta
        List<EstadoCuentaResponseDTO.CuentaEstadoDTO> cuentaEstadoDTOs = new ArrayList<>();

        for (Cuenta cuenta : cuentas) {
            BigDecimal saldoInicial = cuenta.getSaldoInicial();

            // Filtrar movimientos por cuenta
            List<MovimientoResponseDTO> movimientosCuenta = movimientoDTOs.stream()
                    .filter(m -> m.getNumeroCuenta().equals(cuenta.getNumeroCuenta()))
                    .collect(Collectors.toList());

            // Último saldo disponible
            BigDecimal saldoFinal = movimientosCuenta.isEmpty() ? saldoInicial :
                    movimientosCuenta.get(movimientosCuenta.size() - 1).getSaldo();

            // Crear DTO de estado de cuenta
            EstadoCuentaResponseDTO.CuentaEstadoDTO cuentaEstadoDTO = EstadoCuentaResponseDTO.CuentaEstadoDTO.builder()
                    .numeroCuenta(cuenta.getNumeroCuenta())
                    .tipoCuenta(cuenta.getTipoCuenta())
                    .saldoInicial(saldoInicial)
                    .saldoFinal(saldoFinal)
                    .movimientos(movimientosCuenta)
                    .build();

            cuentaEstadoDTOs.add(cuentaEstadoDTO);
        }

        // Crear el DTO de respuesta
        return EstadoCuentaResponseDTO.builder()
                .cliente(cliente.getPersona().getNombre())
                .identificacion(cliente.getPersona().getIdentificacion())
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .cuentas(cuentaEstadoDTOs)
                .totalDebitos(totalDebitos)
                .totalCreditos(totalCreditos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public String generarEstadoCuentaPDF(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        EstadoCuentaResponseDTO estadoCuenta = generarEstadoCuenta(clienteId, fechaInicio, fechaFin);
        byte[] pdfBytes = pdfGenerator.generarEstadoCuentaPDF(estadoCuenta);
        return Base64.getEncoder().encodeToString(pdfBytes);
    }
}