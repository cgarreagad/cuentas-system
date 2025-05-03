package com.cuentasbp.domain.repositories;


import com.cuentasbp.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c JOIN FETCH c.persona WHERE c.persona.identificacion = :identificacion")
    Optional<Cliente> findByPersonaIdentificacion(String identificacion);
}
