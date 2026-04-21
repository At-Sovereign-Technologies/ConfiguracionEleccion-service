package com.selloLegitimo.ConfiguracionEleccion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionCamaraDeptoDto {

	/** Nombre del departamento colombiano, ej: "Antioquia" */
	private String departamento;

	/** Numero de curules asignadas a este departamento, ej: "17" */
	private String curules;

	/** Umbral electoral para este departamento en porcentaje, ej: "0" */
	private String umbral;
}
