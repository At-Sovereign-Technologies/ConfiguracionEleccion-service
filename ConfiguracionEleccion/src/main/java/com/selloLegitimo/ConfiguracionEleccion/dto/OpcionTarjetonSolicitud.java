package com.selloLegitimo.ConfiguracionEleccion.dto;

import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoOpcionTarjeton;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpcionTarjetonSolicitud {

	private String codigo;
	private TipoOpcionTarjeton tipoOpcion;
	private String nombreMostrado;
	private String nombreLista;
	private String partidoPolitico;
	private String circunscripcion;
	private Boolean validada;
}