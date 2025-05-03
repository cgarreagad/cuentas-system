package com.cuentasbp.domain.services.impl;


import com.cuentasbp.domain.entity.Cliente;
import com.cuentasbp.domain.entity.Cuenta;
import com.cuentasbp.domain.repositories.CuentaRepository;
import com.cuentasbp.domain.services.ClienteService;
import com.cuentasbp.domain.services.CuentaService;
import com.cuentasbp.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteService clienteService;

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta findByNumeroCuenta(Long numeroCuenta) {
        return cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "numeroCuenta", numeroCuenta));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> findByClienteId(Long clienteId) {
        return cuentaRepository.findByClienteIdWithPersona(clienteId);
    }

    @Override
    @Transactional
    public Cuenta save(Cuenta cuenta) {
        Cliente cliente = clienteService.findById(cuenta.getCliente().getClienteid());
        cuenta.setCliente(cliente);
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional
    public Cuenta update(Cuenta cuenta) {
        Cuenta existingCuenta = findByNumeroCuenta(cuenta.getNumeroCuenta());
        existingCuenta.setTipoCuenta(cuenta.getTipoCuenta());
        existingCuenta.setEstado(cuenta.getEstado());

        return cuentaRepository.save(existingCuenta);
    }

    @Override
    @Transactional
    public void delete(Long numeroCuenta) {
        Cuenta cuenta = findByNumeroCuenta(numeroCuenta);
        cuenta.setEstado(false);
        cuentaRepository.save(cuenta);
    }
}
