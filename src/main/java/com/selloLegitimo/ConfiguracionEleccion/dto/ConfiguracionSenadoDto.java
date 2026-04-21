package com.selloLegitimo.ConfiguracionEleccion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionSenadoDto {

	/** Umbral electoral en porcentaje, ej: "3.0" */
	private String umbral;

	/** Numero total de curules del Senado, ej: "100" */
	private String curules;

	/** Formula de asignacion: "dhondt" | "sainte-lague" */
	private String formula;
}
