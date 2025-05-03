package com.cuentasbp.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoCuentaResponseDTO {

    private String cliente;
    private String identificacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<CuentaEstadoDTO> cuentas;
    private BigDecimal totalDebitos;
    private BigDecimal totalCreditos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CuentaEstadoDTO {
        private Long numeroCuenta;
        private String tipoCuenta;
        private BigDecimal saldoInicial;
        private BigDecimal saldoFinal;
        private List<MovimientoResponseDTO> movimientos;
    }
}