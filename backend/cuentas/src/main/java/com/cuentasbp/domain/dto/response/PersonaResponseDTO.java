package com.cuentasbp.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaResponseDTO {

    private String identificacion;
    private String nombre;
    private Character genero;
    private Integer edad;
    private String direccion;
    private String telefono;
}