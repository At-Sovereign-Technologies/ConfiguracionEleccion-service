package com.selloLegitimo.ConfiguracionEleccion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SolicitudGenerarTarjeton {

	@NotBlank(message = "La circunscripcion es obligatoria")
	private String circunscripcion;

	@NotEmpty(message = "Debe enviar al menos una opcion validada")
	private List<OpcionTarjetonSolicitud> opcionesValidadas;
}