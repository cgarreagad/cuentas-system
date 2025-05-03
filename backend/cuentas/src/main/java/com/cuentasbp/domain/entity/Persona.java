package com.cuentasbp.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Persona")
public class Persona {

    @Id
    private String identificacion;

    @Column(nullable = false)
    private String nombre;

    private Character genero;

    private Integer edad;

    private String direccion;

    private String telefono;
}