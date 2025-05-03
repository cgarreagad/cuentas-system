package com.cuentasbp.domain.repositories;


import com.cuentasbp.domain.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    List<Cuenta> findByClienteClienteid(Long clienteId);

    @Query("SELECT c FROM Cuenta c JOIN FETCH c.cliente cl JOIN FETCH cl.persona WHERE cl.clienteid = :clienteId")
    List<Cuenta> findByClienteIdWithPersona(Long clienteId);
}
