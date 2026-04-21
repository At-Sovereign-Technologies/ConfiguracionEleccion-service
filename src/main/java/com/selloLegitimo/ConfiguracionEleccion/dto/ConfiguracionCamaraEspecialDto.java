package com.selloLegitimo.ConfiguracionEleccion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionCamaraEspecialDto {

	/** Nombre de la circunscripcion especial, ej: "Comunidades Afrocolombianas" */
	private String nombre;

	/** Descripcion de la circunscripcion */
	private String descripcion;

	/** Numero de curules de esta circunscripcion especial */
	private String curules;

	/** Indica si esta circunscripcion se encuentra habilitada para la eleccion */
	private boolean habilitada;
}
