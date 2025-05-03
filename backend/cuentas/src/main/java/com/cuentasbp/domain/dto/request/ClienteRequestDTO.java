package com.cuentasbp.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {

    @NotNull(message = "La información de la persona es obligatoria")
    private PersonaRequestDTO persona;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    private Boolean estado;
}
