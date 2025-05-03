package com.cuentasbp.api;


import com.cuentasbp.domain.dto.request.ClienteRequestDTO;
import com.cuentasbp.domain.dto.response.ClienteResponseDTO;
import com.cuentasbp.domain.dto.response.ResponseDTO;
import com.cuentasbp.domain.entity.Cliente;
import com.cuentasbp.domain.entity.Persona;
import com.cuentasbp.domain.services.ClienteService;
import com.cuentasbp.domain.services.PersonaService;
import com.cuentasbp.util.mapper.ClienteMapper;
import com.cuentasbp.util.mapper.PersonaMapper;
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
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API para gestionar clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final PersonaService personaService;
    private final ClienteMapper clienteMapper;
    private final PersonaMapper personaMapper;

    @GetMapping
    @Operation(summary = "Obtener todos los clientes")
    public ResponseEntity<ResponseDTO<List<ClienteResponseDTO>>> getAllClientes() {
        List<Cliente> clientes = clienteService.findAll();
        List<ClienteResponseDTO> clienteDTOs = clientes.stream()
                .map(clienteMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseDTO.success(clienteDTOs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un cliente por ID")
    public ResponseEntity<ResponseDTO<ClienteResponseDTO>> getClienteById(
            @Parameter(description = "ID del cliente", required = true) @PathVariable Long id) {
        Cliente cliente = clienteService.findById(id);
        return ResponseEntity.ok(ResponseDTO.success(clienteMapper.toDto(cliente)));
    }

    @GetMapping("/identificacion/{identificacion}")
    @Operation(summary = "Obtener un cliente por identificación")
    public ResponseEntity<ResponseDTO<ClienteResponseDTO>> getClienteByIdentificacion(
            @Parameter(description = "Identificación del cliente", required = true) @PathVariable String identificacion) {
        Cliente cliente = clienteService.findByIdentificacion(identificacion);
        return ResponseEntity.ok(ResponseDTO.success(clienteMapper.toDto(cliente)));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo cliente")
    public ResponseEntity<ResponseDTO<ClienteResponseDTO>> createCliente(
            @Parameter(description = "Datos del cliente", required = true) @Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {

        // Crear o actualizar la persona
        Persona persona = personaMapper.toEntity(clienteRequestDTO.getPersona());
        Persona savedPersona;

        try {
            // Intentar buscar si la persona ya existe
            savedPersona = personaService.findByIdentificacion(persona.getIdentificacion());
            // Si existe, actualizar sus datos
            savedPersona = personaService.update(persona);
        } catch (Exception e) {
            // Si no existe, crearla
            savedPersona = personaService.save(persona);
        }

        // Crear el cliente
        Cliente cliente = clienteMapper.toEntity(clienteRequestDTO, savedPersona);
        Cliente savedCliente = clienteService.save(cliente);

        return new ResponseEntity<>(
                ResponseDTO.success("Cliente creado con éxito", clienteMapper.toDto(savedCliente)),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente existente")
    public ResponseEntity<ResponseDTO<ClienteResponseDTO>> updateCliente(
            @Parameter(description = "ID del cliente", required = true) @PathVariable Long id,
            @Parameter(description = "Datos actualizados del cliente", required = true) @Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {

        // Buscar el cliente
        Cliente existingCliente = clienteService.findById(id);

        // Actualizar la persona
        Persona persona = personaMapper.toEntity(clienteRequestDTO.getPersona());
        persona.setIdentificacion(existingCliente.getPersona().getIdentificacion());
        Persona updatedPersona = personaService.update(persona);

        // Actualizar el cliente
        existingCliente.setContrasena(clienteRequestDTO.getContrasena());
        existingCliente.setEstado(clienteRequestDTO.getEstado());
        Cliente updatedCliente = clienteService.update(existingCliente);

        return ResponseEntity.ok(ResponseDTO.success("Cliente actualizado con éxito", clienteMapper.toDto(updatedCliente)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente (desactivar)")
    public ResponseEntity<ResponseDTO<Void>> deleteCliente(
            @Parameter(description = "ID del cliente", required = true) @PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.ok(ResponseDTO.success("Cliente eliminado con éxito", null));
    }
}
