package com.cuentasbp.api.controllers;

import com.cuentasbp.api.ClienteController;
import com.cuentasbp.domain.dto.request.ClienteRequestDTO;
import com.cuentasbp.domain.dto.request.PersonaRequestDTO;
import com.cuentasbp.domain.dto.response.ClienteResponseDTO;
import com.cuentasbp.domain.dto.response.PersonaResponseDTO;
import com.cuentasbp.domain.entity.Cliente;
import com.cuentasbp.domain.entity.Persona;
import com.cuentasbp.domain.services.ClienteService;
import com.cuentasbp.domain.services.PersonaService;
import com.cuentasbp.util.mapper.ClienteMapper;
import com.cuentasbp.util.mapper.PersonaMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ClienteControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ClienteService clienteService;

    @Mock
    private PersonaService personaService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PersonaMapper personaMapper;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteController clienteController;

    private Persona persona;
    private Cliente cliente;
    private PersonaRequestDTO personaRequestDTO;
    private PersonaResponseDTO personaResponseDTO;
    private ClienteRequestDTO clienteRequestDTO;
    private ClienteResponseDTO clienteResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
        objectMapper.findAndRegisterModules();

        // Configurar Persona
        persona = new Persona();
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

        // Configurar PersonaRequestDTO
        personaRequestDTO = new PersonaRequestDTO();
        personaRequestDTO.setIdentificacion("1234567890");
        personaRequestDTO.setNombre("Juan Pérez");
        personaRequestDTO.setGenero("M");
        personaRequestDTO.setEdad(30);

        // Configurar PersonaResponseDTO
        personaResponseDTO = new PersonaResponseDTO();
        personaResponseDTO.setIdentificacion("1234567890");
        personaResponseDTO.setNombre("Juan Pérez");
        personaResponseDTO.setGenero('M');
        personaResponseDTO.setEdad(30);

        // Configurar ClienteRequestDTO
        clienteRequestDTO = new ClienteRequestDTO();
        clienteRequestDTO.setPersona(personaRequestDTO);
        clienteRequestDTO.setContrasena("password");
        clienteRequestDTO.setEstado(true);

        // Configurar ClienteResponseDTO
        clienteResponseDTO = new ClienteResponseDTO();
        clienteResponseDTO.setClienteId(1L);
        clienteResponseDTO.setEstado(true);
        clienteResponseDTO.setPersona(personaResponseDTO);
    }

    @Test
    void getAllClientes_debeRetornarListaDeClientes() throws Exception {
        List<Cliente> clientes = Arrays.asList(cliente);
        when(clienteService.findAll()).thenReturn(clientes);
        when(clienteMapper.toDto(cliente)).thenReturn(clienteResponseDTO);

        mockMvc.perform(get("/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].clienteId").value(1))
                .andExpect(jsonPath("$.data[0].estado").value(true))
                .andExpect(jsonPath("$.data[0].persona.identificacion").value("1234567890"));

        verify(clienteService, times(1)).findAll();
    }

    @Test
    void getClienteById_debeRetornarCliente() throws Exception {
        when(clienteService.findById(1L)).thenReturn(cliente);
        when(clienteMapper.toDto(cliente)).thenReturn(clienteResponseDTO);

        mockMvc.perform(get("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.clienteId").value(1))
                .andExpect(jsonPath("$.data.estado").value(true))
                .andExpect(jsonPath("$.data.persona.identificacion").value("1234567890"));

        verify(clienteService, times(1)).findById(1L);
    }

    @Test
    void createCliente_debeCrearCliente() throws Exception {
        // Configurar mocks en el orden correcto
        when(personaMapper.toEntity(any(PersonaRequestDTO.class))).thenReturn(persona);
        when(personaService.findByIdentificacion("1234567890")).thenReturn(null); // Persona no existe
        when(personaService.update(any(Persona.class))).thenReturn(persona);

        // Configurar el mock para clienteMapper.toEntity con argumentos específicos
        doReturn(cliente).when(clienteMapper).toEntity(
                argThat(dto -> dto.getPersona() != null &&
                        dto.getPersona().getIdentificacion().equals("1234567890") &&
                        dto.getContrasena().equals("password") &&
                        dto.getEstado() == true),
                argThat(p -> p != null &&
                        p.getIdentificacion().equals("1234567890") &&
                        p.getNombre().equals("Juan Pérez") &&
                        p.getGenero() == 'M' &&
                        p.getEdad() == 30)
        );

        when(clienteService.save(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toDto(cliente)).thenReturn(clienteResponseDTO);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Cliente creado con éxito"))
                .andExpect(jsonPath("$.data.clienteId").value(1))
                .andExpect(jsonPath("$.data.estado").value(true))
                .andExpect(jsonPath("$.data.persona.identificacion").value("1234567890"))
                .andExpect(jsonPath("$.data.persona.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.data.persona.genero").value("M"))
                .andExpect(jsonPath("$.data.persona.edad").value(30));

        verify(personaMapper, times(1)).toEntity(any(PersonaRequestDTO.class));
        verify(personaService, times(1)).findByIdentificacion("1234567890");
        verify(personaService, times(1)).update(any(Persona.class));
        verify(clienteMapper, times(1)).toEntity(
                any(ClienteRequestDTO.class),
                argThat(p -> p != null &&
                        p.getIdentificacion().equals("1234567890") &&
                        p.getNombre().equals("Juan Pérez") &&
                        p.getGenero() == 'M' &&
                        p.getEdad() == 30)
        );
        verify(clienteService, times(1)).save(any(Cliente.class));
        verify(clienteMapper, times(1)).toDto(cliente);
    }

    @Test
    void updateCliente_debeActualizarCliente() throws Exception {
        Cliente clienteExistente = new Cliente();
        clienteExistente.setClienteid(1L);
        clienteExistente.setContrasena("password");
        clienteExistente.setEstado(true);
        clienteExistente.setPersona(persona);

        when(clienteService.findById(1L)).thenReturn(clienteExistente);
        when(personaMapper.toEntity(any(PersonaRequestDTO.class))).thenReturn(persona);
        when(clienteService.update(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toDto(cliente)).thenReturn(clienteResponseDTO);

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Cliente actualizado con éxito"))
                .andExpect(jsonPath("$.data.clienteId").value(1))
                .andExpect(jsonPath("$.data.estado").value(true))
                .andExpect(jsonPath("$.data.persona.identificacion").value("1234567890"));

        verify(clienteService, times(1)).update(any(Cliente.class));
    }

    @Test
    void deleteCliente_debeEliminarCliente() throws Exception {
        doNothing().when(clienteService).delete(1L);

        mockMvc.perform(delete("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Cliente eliminado con éxito"));

        verify(clienteService, times(1)).delete(1L);
    }
}