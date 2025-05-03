package com.cuentasbp.api;


import com.cuentasbp.domain.dto.request.CuentaRequestDTO;
import com.cuentasbp.domain.dto.request.EstadoCuentaRequestDTO;
import com.cuentasbp.domain.dto.response.CuentaResponseDTO;
import com.cuentasbp.domain.dto.response.EstadoCuentaResponseDTO;
import com.cuentasbp.domain.dto.response.ResponseDTO;
import com.cuentasbp.domain.entity.Cliente;
import com.cuentasbp.domain.entity.Cuenta;
import com.cuentasbp.domain.services.ClienteService;
import com.cuentasbp.domain.services.CuentaService;
import com.cuentasbp.domain.services.ReporteService;
import com.cuentasbp.util.mapper.CuentaMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "API para gestionar cuentas bancarias")
public class CuentaController {

    private final CuentaService cuentaService;
    private final ClienteService clienteService;
    private final ReporteService reporteService;
    private final CuentaMapper cuentaMapper;

    @GetMapping
    @Operation(summary = "Obtener todas las cuentas")
    public ResponseEntity<ResponseDTO<List<CuentaResponseDTO>>> getAllCuentas() {
        List<Cuenta> cuentas = cuentaService.findAll();
        List<CuentaResponseDTO> cuentaDTOs = cuentas.stream()
                .map(cuentaMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseDTO.success(cuentaDTOs));
    }

    @GetMapping("/{numeroCuenta}")
    @Operation(summary = "Obtener una cuenta por número")
    public ResponseEntity<ResponseDTO<CuentaResponseDTO>> getCuentaByNumeroCuenta(
            @Parameter(description = "Número de la cuenta", required = true) @PathVariable Long numeroCuenta) {
        Cuenta cuenta = cuentaService.findByNumeroCuenta(numeroCuenta);
        return ResponseEntity.ok(ResponseDTO.success(cuentaMapper.toDto(cuenta)));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener cuentas de un cliente")
    public ResponseEntity<ResponseDTO<List<CuentaResponseDTO>>> getCuentasByClienteId(
            @Parameter(description = "ID del cliente", required = true) @PathVariable Long clienteId) {
        List<Cuenta> cuentas = cuentaService.findByClienteId(clienteId);
        List<CuentaResponseDTO> cuentaDTOs = cuentas.stream()
                .map(cuentaMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseDTO.success(cuentaDTOs));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva cuenta")
    public ResponseEntity<ResponseDTO<CuentaResponseDTO>> createCuenta(
            @Parameter(description = "Datos de la cuenta", required = true) @Valid @RequestBody CuentaRequestDTO cuentaRequestDTO) {

        Cliente cliente = clienteService.findById(cuentaRequestDTO.getClienteId());
        Cuenta cuenta = cuentaMapper.toEntity(cuentaRequestDTO, cliente);
        Cuenta savedCuenta = cuentaService.save(cuenta);

        return new ResponseEntity<>(
                ResponseDTO.success("Cuenta creada con éxito", cuentaMapper.toDto(savedCuenta)),
                HttpStatus.CREATED);
    }

    @PutMapping("/{numeroCuenta}")
    @Operation(summary = "Actualizar una cuenta existente")
    public ResponseEntity<ResponseDTO<CuentaResponseDTO>> updateCuenta(
            @Parameter(description = "Número de la cuenta", required = true) @PathVariable Long numeroCuenta,
            @Parameter(description = "Datos actualizados de la cuenta", required = true) @Valid @RequestBody CuentaRequestDTO cuentaRequestDTO) {

        Cuenta existingCuenta = cuentaService.findByNumeroCuenta(numeroCuenta);
        existingCuenta.setTipoCuenta(cuentaRequestDTO.getTipoCuenta());
        existingCuenta.setEstado(cuentaRequestDTO.getEstado());

        Cuenta updatedCuenta = cuentaService.update(existingCuenta);

        return ResponseEntity.ok(ResponseDTO.success("Cuenta actualizada con éxito", cuentaMapper.toDto(updatedCuenta)));
    }

    @DeleteMapping("/{numeroCuenta}")
    @Operation(summary = "Eliminar una cuenta (desactivar)")
    public ResponseEntity<ResponseDTO<Void>> deleteCuenta(
            @Parameter(description = "Número de la cuenta", required = true) @PathVariable Long numeroCuenta) {
        cuentaService.delete(numeroCuenta);
        return ResponseEntity.ok(ResponseDTO.success("Cuenta eliminada con éxito", null));
    }

    @PostMapping("/estado-cuenta")
    @Operation(summary = "Generar estado de cuenta")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> generarEstadoCuenta(
            @Parameter(description = "Datos para generar el estado de cuenta", required = true) @Valid @RequestBody EstadoCuentaRequestDTO request) {

        // Generar el estado de cuenta en formato JSON
        EstadoCuentaResponseDTO estadoCuenta = reporteService.generarEstadoCuenta(
                request.getClienteId(), request.getFechaInicio(), request.getFechaFin());

        // Generar el estado de cuenta en formato PDF (Base64)
        String pdfBase64 = reporteService.generarEstadoCuentaPDF(
                request.getClienteId(), request.getFechaInicio(), request.getFechaFin());

        // Crear respuesta combinada
        Map<String, Object> response = new HashMap<>();
        response.put("json", estadoCuenta);
        response.put("pdf", pdfBase64);

        return ResponseEntity.ok(ResponseDTO.success("Estado de cuenta generado con éxito", response));
    }
}
