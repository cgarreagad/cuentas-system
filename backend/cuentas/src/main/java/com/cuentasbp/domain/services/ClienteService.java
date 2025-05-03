package com.cuentasbp.domain.services;

import com.cuentasbp.domain.entity.Cliente;

import java.util.List;

public interface ClienteService {

    List<Cliente> findAll();

    Cliente findById(Long id);

    Cliente findByIdentificacion(String identificacion);

    Cliente save(Cliente cliente);

    Cliente update(Cliente cliente);

    void delete(Long id);
}