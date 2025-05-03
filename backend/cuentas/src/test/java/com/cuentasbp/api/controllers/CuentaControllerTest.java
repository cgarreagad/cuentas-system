package com.cuentasbp.api.controllers;

import com.cuentasbp.api.CuentaController;
import com.cuentasbp.domain.dto.request.CuentaRequestDTO;
import com.cuentasbp.domain.dto.response.CuentaResponseDTO;
import com.cuentasbp.domain.entity.Cliente;
import com.cuentasbp.domain.entity.Cuenta;
import com.cuentasbp.domain.entity.Persona;
import com.cuentasbp.domain.services.ClienteService;
import com.cuentasbp.domain.services.CuentaService;
import com.cuentasbp.util.mapper.CuentaMapper;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CuentaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CuentaService cuentaService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private CuentaController cuentaController;

    private Cliente cliente;
    private Cuenta cuenta;
    private CuentaRequestDTO cuentaRequestDTO;
    private CuentaResponseDTO cuentaResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cuentaController).build();
        objectMapper.findAndRegisterModules();

        // Configurar Persona
        Persona persona = new Persona();
        persona.setIdentificacion("1234567890");
        persona.setNombre("Juan Pérez");
        persona.setGenero('M');
        persona.setEdad(30);

        // Configurar Cliente
        cliente = new Cliente();
        cliente.setClienteid(1L);
        cliente.setContrasena("password");
        cliente.setEstado(true);
        cliente.setPersona(persona);

        // Configurar Cuenta
        cuenta = new Cuenta();
        cuenta.setNumeroCuenta(1234567890L);
        cuenta.setTipoCuenta("AHORRO");
        cuenta.setSaldoInicial(new BigDecimal("1000.0"));
        cuenta.setEstado(true);
        cuenta.setCliente(cliente);

        // Configurar CuentaRequestDTO
        cuentaRequestDTO = new CuentaRequestDTO();
        cuentaRequestDTO.setTipoCuenta("AHORRO");
        cuentaRequestDTO.setSaldoInicial(new BigDecimal("1000.0"));
        cuentaRequestDTO.setEstado(true);
        cuentaRequestDTO.setClienteId(1L);

        // Configurar CuentaResponseDTO
        cuentaResponseDTO = new CuentaResponseDTO();
        cuentaResponseDTO.setNumeroCuenta(1234567890L);
        cuentaResponseDTO.setTipoCuenta("AHORRO");
        cuentaResponseDTO.setSaldoInicial(new BigDecimal("1000.0"));
        cuentaResponseDTO.setEstado(true);
    }

    @Test
    void getAllCuentas_debeRetornarListaDeCuentas() throws Exception {
        List<Cuenta> cuentas = Arrays.asList(cuenta);
        when(cuentaService.findAll()).thenReturn(cuentas);
        when(cuentaMapper.toDto(cuenta)).thenReturn(cuentaResponseDTO);

        mockMvc.perform(get("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].numeroCuenta").value(1234567890))
                .andExpect(jsonPath("$.data[0].tipoCuenta").value("AHORRO"));

        verify(cuentaService, times(1)).findAll();
    }

    @Test
    void getCuentaByNumeroCuenta_debeRetornarCuenta() throws Exception {
        when(cuentaService.findByNumeroCuenta(1234567890L)).thenReturn(cuenta);
        when(cuentaMapper.toDto(cuenta)).thenReturn(cuentaResponseDTO);

        mockMvc.perform(get("/cuentas/1234567890")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.numeroCuenta").value(1234567890))
                .andExpect(jsonPath("$.data.tipoCuenta").value("AHORRO"));

        verify(cuentaService, times(1)).findByNumeroCuenta(1234567890L);
    }

    @Test
    void createCuenta_debeCrearCuenta() throws Exception {
        when(clienteService.findById(1L)).thenReturn(cliente);
        when(cuentaMapper.toEntity(any(CuentaRequestDTO.class), any(Cliente.class))).thenReturn(cuenta);
        when(cuentaService.save(any(Cuenta.class))).thenReturn(cuenta);
        when(cuentaMapper.toDto(cuenta)).thenReturn(cuentaResponseDTO);

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Cuenta creada con éxito"))
                .andExpect(jsonPath("$.data.numeroCuenta").value(1234567890))
                .andExpect(jsonPath("$.data.tipoCuenta").value("AHORRO"));

        verify(clienteService, times(1)).findById(1L);
        verify(cuentaMapper, times(1)).toEntity(any(CuentaRequestDTO.class), any(Cliente.class));
        verify(cuentaService, times(1)).save(any(Cuenta.class));
        verify(cuentaMapper, times(1)).toDto(cuenta);
    }

    @Test
    void updateCuenta_debeActualizarCuenta() throws Exception {
        when(cuentaService.findByNumeroCuenta(1234567890L)).thenReturn(cuenta);
        when(cuentaService.update(any(Cuenta.class))).thenReturn(cuenta);
        when(cuentaMapper.toDto(cuenta)).thenReturn(cuentaResponseDTO);

        mockMvc.perform(put("/cuentas/1234567890")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Cuenta actualizada con éxito"))
                .andExpect(jsonPath("$.data.numeroCuenta").value(1234567890))
                .andExpect(jsonPath("$.data.tipoCuenta").value("AHORRO"));

        verify(cuentaService, times(1)).update(any(Cuenta.class));
    }

    @Test
    void deleteCuenta_debeEliminarCuenta() throws Exception {
        doNothing().when(cuentaService).delete(1234567890L);

        mockMvc.perform(delete("/cuentas/1234567890")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Cuenta eliminada con éxito"));

        verify(cuentaService, times(1)).delete(1234567890L);
    }
}