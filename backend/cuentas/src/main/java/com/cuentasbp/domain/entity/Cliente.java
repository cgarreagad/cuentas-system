package com.cuentasbp.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clienteid;

    @Column(name = "contraseña", nullable = false)
    private String contrasena;

    private Boolean estado;

    @OneToOne
    @JoinColumn(name = "identificacion", referencedColumnName = "identificacion", nullable = false)
    private Persona persona;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Cuenta> cuentas;
}