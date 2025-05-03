package com.cuentasbp.domain.services.impl;


import com.cuentasbp.domain.entity.Cliente;
import com.cuentasbp.domain.repositories.ClienteRepository;
import com.cuentasbp.domain.services.ClienteService;
import com.cuentasbp.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente findByIdentificacion(String identificacion) {
        return clienteRepository.findByPersonaIdentificacion(identificacion)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "identificacion", identificacion));
    }

    @Override
    @Transactional
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public Cliente update(Cliente cliente) {
        Cliente existingCliente = findById(cliente.getClienteid());
        existingCliente.setContrasena(cliente.getContrasena());
        existingCliente.setEstado(cliente.getEstado());

        return clienteRepository.save(existingCliente);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Cliente cliente = findById(id);
        cliente.setEstado(false);
        clienteRepository.save(cliente);
    }
}
