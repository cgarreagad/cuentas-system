package com.cuentasbp.domain.services;

import com.cuentasbp.domain.entity.Cuenta;

import java.util.List;

public interface CuentaService {

    List<Cuenta> findAll();

    Cuenta findByNumeroCuenta(Long numeroCuenta);

    List<Cuenta> findByClienteId(Long clienteId);

    Cuenta save(Cuenta cuenta);

    Cuenta update(Cuenta cuenta);

    void delete(Long numeroCuenta);
}
