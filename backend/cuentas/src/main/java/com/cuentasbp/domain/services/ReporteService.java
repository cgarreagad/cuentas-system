package com.cuentasbp.domain.services;

import com.cuentasbp.domain.dto.response.EstadoCuentaResponseDTO;

import java.time.LocalDate;

public interface ReporteService {

    EstadoCuentaResponseDTO generarEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);

    String generarEstadoCuentaPDF(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
