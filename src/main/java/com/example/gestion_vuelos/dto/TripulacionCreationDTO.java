package com.example.gestion_vuelos.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class TripulacionCreationDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El cargo es obligatorio")
    @Pattern(regexp = "^(Piloto|Copiloto|Sobrecargo|Tripulante de cabina)$",
            message = "Cargo inválido. Valores permitidos: Piloto, Copiloto, Sobrecargo, Tripulante de cabina")
    private String cargo;

    @NotNull(message = "El ID de la aerolínea es obligatorio")
    private Integer idAerolinea;
}
