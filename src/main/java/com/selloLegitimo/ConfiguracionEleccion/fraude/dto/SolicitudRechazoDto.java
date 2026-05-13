package com.selloLegitimo.ConfiguracionEleccion.fraude.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SolicitudRechazoDto {

	@NotBlank(message = "El motivo del rechazo es obligatorio")
	@Size(max = 500, message = "El motivo no puede superar 500 caracteres")
	private String motivo;
}
