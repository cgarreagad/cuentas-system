package com.cuentasbp.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaRequestDTO {

    @NotBlank(message = "La identificación es obligatoria")
    @Size(max = 20, message = "La identificación no puede tener más de 20 caracteres")
    private String identificacion;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @Pattern(regexp = "[MFO]", message = "El género debe ser M, F u O")
    private String genero;

    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;

    @Size(max = 200, message = "La dirección no puede tener más de 200 caracteres")
    private String direccion;

    @Size(max = 20, message = "El teléfono no puede tener más de 20 caracteres")
    private String telefono;
}