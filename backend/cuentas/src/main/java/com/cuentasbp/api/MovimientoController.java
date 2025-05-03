package com.cuentasbp.api;


import com.cuentasbp.domain.dto.request.MovimientoRequestDTO;
import com.cuentasbp.domain.dto.response.MovimientoResponseDTO;
import com.cuentasbp.domain.dto.response.ResponseDTO;
import com.cuentasbp.domain.entity.Movimiento;
import com.cuentasbp.domain.services.MovimientoService;
import com.cuentasbp.util.mapper.MovimientoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "API para gestionar movimientos bancarios")
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final MovimientoMapper movimientoMapper;

    @GetMapping
    @Operation(summary = "Obtener todos los movimientos")
    public ResponseEntity<ResponseDTO<List<MovimientoResponseDTO>>> getAllMovimientos() {
        List<Movimiento> movimientos = movimientoService.findAll();
        List<MovimientoResponseDTO> movimientoDTOs = movimientos.stream()
                .map(movimientoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseDTO.success(movimientoDTOs));
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener un movimiento por código")
    public ResponseEntity<ResponseDTO<MovimientoResponseDTO>> getMovimientoByCodigo(
            @Parameter(description = "Código del movimiento", required = true) @PathVariable Long codigo) {
        Movimiento movimiento = movimientoService.findByCodigo(codigo);
        return ResponseEntity.ok(ResponseDTO.success(movimientoMapper.toDto(movimiento)));
    }

    @GetMapping("/cuenta/{numeroCuenta}")
    @Operation(summary = "Obtener movimientos de una cuenta")
    public ResponseEntity<ResponseDTO<List<MovimientoResponseDTO>>> getMovimientosByNumeroCuenta(
            @Parameter(description = "Número de la cuenta", required = true) @PathVariable Long numeroCuenta) {
        List<Movimiento> movimientos = movimientoService.findByNumeroCuenta(numeroCuenta);
        List<MovimientoResponseDTO> movimientoDTOs = movimientos.stream()
                .map(movimientoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseDTO.success(movimientoDTOs));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo movimiento")
    public ResponseEntity<ResponseDTO<MovimientoResponseDTO>> createMovimiento(
            @Parameter(description = "Datos del movimiento", required = true) @Valid @RequestBody MovimientoRequestDTO movimientoRequestDTO) {

        Movimiento movimiento = movimientoService.realizarMovimiento(
                movimientoRequestDTO.getTipoMovimiento(),
                movimientoRequestDTO.getValor(),
                movimientoRequestDTO.getNumeroCuenta());

        return new ResponseEntity<>(
                ResponseDTO.success("Movimiento realizado con éxito", movimientoMapper.toDto(movimiento)),
                HttpStatus.CREATED);
    }
}
