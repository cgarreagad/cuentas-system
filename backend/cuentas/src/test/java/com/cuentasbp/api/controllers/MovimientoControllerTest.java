package com.cuentasbp.api.controllers;

import com.cuentasbp.api.MovimientoController;
import com.cuentasbp.domain.dto.request.MovimientoRequestDTO;
import com.cuentasbp.domain.dto.response.MovimientoResponseDTO;
import com.cuentasbp.domain.entity.Cliente;
import com.cuentasbp.domain.entity.Cuenta;
import com.cuentasbp.domain.entity.Movimiento;
import com.cuentasbp.domain.entity.Persona;
import com.cuentasbp.domain.services.CuentaService;
import com.cuentasbp.domain.services.MovimientoService;
import com.cuentasbp.util.mapper.MovimientoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MovimientoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MovimientoService movimientoService;

    @Mock
    private CuentaService cuentaService;

    @Mock
    private MovimientoMapper movimientoMapper;

    @InjectMocks
    private MovimientoController movimientoController;

    private Cuenta cuenta;
    private Movimiento movimiento;
    private MovimientoRequestDTO movimientoRequestDTO;
    private MovimientoResponseDTO movimientoResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(movimientoController).build();
        objectMapper.findAndRegisterModules();

        // Configurar Persona
        Persona persona = new Persona();
        persona.setIdentificacion("1234567890");
        persona.setNombre("Juan Pérez");
        persona.setGenero('M');
        persona.setEdad(30);

        // Configurar Cliente
        Cliente cliente = new Cliente();
        cliente.setClienteid(1L);
        cliente.setContrasena("password");
        cliente.setEstado(true);
        cliente.setPersona(persona);

        // Configurar Cuenta
        cuenta = new Cuenta();
        cuenta.setNumeroCuenta(1234567890L);
        cuenta.setTipoCuenta("Ahorros");
        cuenta.setSaldoInicial(new BigDecimal("1000.0"));
        cuenta.setEstado(true);
        cuenta.setCliente(cliente);

        // Configurar Movimiento
        movimiento = new Movimiento();
        movimiento.setCodigo(1L);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento("DEPOSITO");
        movimiento.setValor(new BigDecimal("500.0"));
        movimiento.setSaldo(new BigDecimal("1500.0"));
        movimiento.setCuenta(cuenta);

        // Configurar MovimientoRequestDTO
        movimientoRequestDTO = new MovimientoRequestDTO();
        movimientoRequestDTO.setTipoMovimiento("DEPOSITO");
        movimientoRequestDTO.setValor(new BigDecimal("500.0"));
        movimientoRequestDTO.setNumeroCuenta(1234567890L);

        // Configurar MovimientoResponseDTO
        movimientoResponseDTO = new MovimientoResponseDTO();
        movimientoResponseDTO.setCodigo(1L);
        movimientoResponseDTO.setFecha(LocalDateTime.now());
        movimientoResponseDTO.setTipoMovimiento("DEPOSITO");
        movimientoResponseDTO.setValor(new BigDecimal("500.0"));
        movimientoResponseDTO.setSaldo(new BigDecimal("1500.0"));
        movimientoResponseDTO.setNumeroCuenta(1234567890L);
        movimientoResponseDTO.setCliente("Juan Pérez");
    }

    @Test
    void getAllMovimientos_debeRetornarListaDeMovimientos() throws Exception {
        List<Movimiento> movimientos = Arrays.asList(movimiento);
        when(movimientoService.findAll()).thenReturn(movimientos);
        when(movimientoMapper.toDto(movimiento)).thenReturn(movimientoResponseDTO);

        mockMvc.perform(get("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].codigo").value(1))
                .andExpect(jsonPath("$.data[0].tipoMovimiento").value("DEPOSITO"))
                .andExpect(jsonPath("$.data[0].valor").value(500.0));

        verify(movimientoService, times(1)).findAll();
    }

    @Test
    void getMovimientoByCodigo_debeRetornarMovimiento() throws Exception {
        when(movimientoService.findByCodigo(1L)).thenReturn(movimiento);
        when(movimientoMapper.toDto(movimiento)).thenReturn(movimientoResponseDTO);

        mockMvc.perform(get("/movimientos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.codigo").value(1))
                .andExpect(jsonPath("$.data.tipoMovimiento").value("DEPOSITO"))
                .andExpect(jsonPath("$.data.valor").value(500.0));

        verify(movimientoService, times(1)).findByCodigo(1L);
    }

    @Test
    void realizarMovimiento_debeCrearMovimiento() throws Exception {
        when(movimientoService.realizarMovimiento("DEPOSITO", new BigDecimal("500.0"), 1234567890L))
                .thenReturn(movimiento);
        when(movimientoMapper.toDto(movimiento)).thenReturn(movimientoResponseDTO);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimientoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Movimiento realizado con éxito"))
                .andExpect(jsonPath("$.data.codigo").value(1))
                .andExpect(jsonPath("$.data.tipoMovimiento").value("DEPOSITO"))
                .andExpect(jsonPath("$.data.valor").value(500.0));

        verify(movimientoService, times(1))
                .realizarMovimiento("DEPOSITO", new BigDecimal("500.0"), 1234567890L);
        verify(movimientoMapper, times(1)).toDto(movimiento);
    }

    @Test
    void getMovimientosByNumeroCuenta_debeRetornarListaDeMovimientos() throws Exception {
        List<Movimiento> movimientos = Arrays.asList(movimiento);
        when(movimientoService.findByNumeroCuenta(1234567890L)).thenReturn(movimientos);
        when(movimientoMapper.toDto(movimiento)).thenReturn(movimientoResponseDTO);

        mockMvc.perform(get("/movimientos/cuenta/1234567890")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].codigo").value(1))
                .andExpect(jsonPath("$.data[0].tipoMovimiento").value("DEPOSITO"))
                .andExpect(jsonPath("$.data[0].valor").value(500.0));

        verify(movimientoService, times(1)).findByNumeroCuenta(1234567890L);
    }
}